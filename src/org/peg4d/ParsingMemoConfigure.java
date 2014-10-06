package org.peg4d;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class ParsingMemoConfigure {
	public final static ParsingObject NonTransition = new ParsingObject(null, null, 0);
	public static boolean NoMemo = false;
	public static boolean PackratParsing = false;
	public static boolean FifoPackratParsing = false;
	public static int     BacktrackBufferSize = 256;
	public static boolean Tracing = true;
	public static boolean VerboseMemo = false;
	
	UList<MemoMatcher> memoList = new UList<MemoMatcher>(new MemoMatcher[16]);
	
	ParsingMemoConfigure() {
	}
	
	HashMap<Integer, MemoPoint> memoMap = new HashMap<Integer, MemoPoint>();
    
	MemoPoint getMemoPoint(ParsingExpression e) {
		Integer key = e.uniqueId;
		assert(e.uniqueId != 0);
		MemoPoint m = this.memoMap.get(key);
		if(m == null) {
			m = new MemoPoint(e);
			m.memoPoint = this.memoMap.size();
			this.memoMap.put(key, m);
		}
//		else {
//			if(VerboseMemo) {
//				System.out.println("unified memo: \n\t" + m.e+ "\n\t" + e);
//			}
//		}
		return m;
	}

	void setMemoMatcher(ParsingExpression e, MemoMatcher m) {
		memoList.add(m);
	}

	class MemoPoint {
		ParsingExpression e;
		int memoPoint;
		int memoHit = 0;
		long hitLength = 0;
		int  maxLength = 0;
		int memoMiss = 0;
		MemoPoint(ParsingExpression e) {
			this.e = e;
		}

		final double ratio() {
			if(this.memoMiss == 0.0) return 0.0;
			return (double)this.memoHit / this.memoMiss;
		}

		final double length() {
			if(this.memoHit == 0) return 0.0;
			return (double)this.hitLength / this.memoHit;
		}

		final int count() {
			return this.memoMiss + this.memoHit;
		}

		protected boolean checkUseless() {
			if(this.memoMiss == 32) {
				if(this.memoHit < 2) {          
					return true;
				}
			}
			if(this.memoMiss % 64 == 0) {
				if(this.memoHit == 0) {
					return true;
				}
//				if(this.hitLength < this.memoHit) {
//					enableMemo = false;
//					disabledMemo();
//					return;
//				}
				if(this.memoMiss / this.memoHit > 10) {
					return true;
				}
			}
			return false;
		}

		void hit(int consumed) {
			this.memoHit += 1;
			this.hitLength += consumed;
			if(this.maxLength < consumed) {
				this.maxLength = consumed;
			}
		}
	}
	
	void exploitMemo1(ParsingExpression e) {
		for(int i = 0; i < e.size(); i++) {
			exploitMemo1(e.get(i));
		}
		if(!(e.matcher instanceof MemoMatcher)) {
			if(e instanceof ParsingConnector) {
				ParsingConnector ne = (ParsingConnector)e;
				MemoPoint mp = getMemoPoint(ne.inner);
				MemoMatcher m = new ConnectorMemoMatcher(ne, mp);
				memoList.add(m);
				ne.matcher = m;
			}
			if(e instanceof NonTerminal) {
				NonTerminal ne = (NonTerminal)e;
				if(ne.getRule().type == ParsingRule.LexicalRule) {
					ParsingExpression deref = Optimizer2.resolveNonTerminal(ne);
					MemoPoint mp = getMemoPoint(deref);
					MemoMatcher m = new NonTerminalMemoMatcher(ne, mp);
					memoList.add(m);
					ne.matcher = m;
				}
			}
		}
	}

	void exploitMemo(ParsingRule rule) {
		if(!NoMemo) {
			for(ParsingRule r : rule.subRule()) {
				exploitMemo1(r.expr);
			}
		}
		for(MemoMatcher m : memoList) {
			//System.out.println(m);
		}
	}

	void show2(ParsingStatistics stat) {
		if(VerboseMemo) {
			for(int i = 0; i < memoList.size() - 1; i++) {
				for(int j = i + 1; j < memoList.size(); j++) {
					if(memoList.ArrayValues[i].compareTo(memoList.ArrayValues[j]) > 0) {
						MemoMatcher m = memoList.ArrayValues[i];
						memoList.ArrayValues[i] = memoList.ArrayValues[j];
						memoList.ArrayValues[j] = m;
					}
				}
			}
			if(stat != null) {
				int hit = 0;
				int miss = 0;
				for(MemoMatcher m : memoList) {
					System.out.println(m);
					hit += m.memo.memoHit;
					miss += m.memo.memoMiss;
				}
				System.out.println("Total: " + ((double)hit / miss) + " WorstCaseBackTrack=" + stat.WorstBacktrackSize);
			}
		}
	}
	
	ParsingMemo newMemo() {
		if(ParsingMemoConfigure.NoMemo) {
			return new NoParsingMemo();
		}
		if(ParsingMemoConfigure.PackratParsing) {
			return new PackratParsingMemo(512);
		}
		if(ParsingMemoConfigure.FifoPackratParsing) {
			return new FifoPackratParsingMemo(ParsingMemoConfigure.BacktrackBufferSize);
		}
		return new TracingPackratParsingMemo(ParsingMemoConfigure.BacktrackBufferSize, this.memoList.size());
	}
	
	abstract class MemoMatcher extends ParsingMatcher {
        final MemoPoint memo;
		ParsingExpression holder = null;
		ParsingExpression key = null;
		ParsingMatcher matchRef = null;
		boolean enableMemo = true;

		MemoMatcher(MemoPoint memo) {
			this.memo = memo;
		}

		private int memoPoint() {
			return memo.memoPoint;
			//return key.uniqueId;
		}
		
		final boolean memoMatch(ParsingContext context, ParsingMatcher ma) {
			long pos = context.getPosition();
			MemoEntry m = context.getMemo(pos, memoPoint());
			if(m != null) {
				this.memo.hit(m.consumed);
				context.setPosition(pos + m.consumed);
				if(m.result != ParsingMemoConfigure.NonTransition) {
					context.left = m.result;
				}
				return !(context.isFailure());
			}
			ParsingObject left = context.left;
			boolean b = ma.simpleMatch(context);
			int length = (int)(context.getPosition() - pos);
			context.setMemo(pos, memoPoint(), (context.left == left) ? ParsingMemoConfigure.NonTransition : context.left, length);
			this.memo.memoMiss += 1;
			if(Tracing && memo.checkUseless()) {
				enableMemo = false;
				disabledMemo();
			}
			left = null;
			return b;
		}
		
		public int compareTo(MemoMatcher m) {
			if(this.memo.ratio() == m.memo.ratio()) {
				return this.memo.count() > m.memo.count() ? 1 : -1;
			}
			return (this.memo.ratio() > m.memo.ratio()) ? 1 : -1;
		}

		void disabledMemo() {
			this.holder.matcher = new DisabledMemoMatcher(this);
		}
				
		@Override
		public String toString() {
			return String.format("MEMO[%d,%s] r=%2.5f #%d len=%.2f %d %s", 
                    this.memo.memoPoint, this.enableMemo,this.memo.ratio(), 
                    this.memo.count(), this.memo.length(), this.memo.maxLength, holder);
		}
	}
	
	class ConnectorMemoMatcher extends MemoMatcher {
		int index;
		ConnectorMemoMatcher(ParsingConnector holder, MemoPoint memo) {
			super(memo);
			this.holder = holder;
			this.key = holder.inner;
			this.matchRef = holder.inner.matcher;
			this.index = holder.index;
		}
		
		@Override
		void disabledMemo() {
			this.holder.matcher = holder;
		}
		
		@Override
		public boolean simpleMatch(ParsingContext context) {
			ParsingObject left = context.left;
			int mark = context.markObjectStack();
			if(this.memoMatch(context, this.matchRef)) {
				if(context.left != left) {
					//System.out.println("Linked: " + this.holder + " " + left.oid + " => " + context.left.oid);
					context.commitLinkLog(mark, context.left);
					context.logLink(left, this.index, context.left);
				}
				else {
					System.out.println("FIXME nothing linked: " + this.holder + " " + left.oid + " => " + context.left.oid);
					context.abortLinkLog(mark);					
				}
				context.left = left;
				left = null;
				return true;
			}
			context.abortLinkLog(mark);
			left = null;
			return false;
		}
	}

	class NonTerminalMemoMatcher extends MemoMatcher {
		NonTerminalMemoMatcher(NonTerminal inner, MemoPoint memo) {
			super(memo);
			this.holder = inner;
			this.key = Optimizer2.resolveNonTerminal(inner);
			this.matchRef = key.matcher;
		}
		
		@Override
		public boolean simpleMatch(ParsingContext context) {
			return memoMatch(context, this.matchRef);
		}
	}

	class DisabledMemoMatcher extends MemoMatcher {
		DisabledMemoMatcher(MemoMatcher m) {
			super(m.memo);
			this.holder = m.holder;
			this.matchRef = m.matchRef;
			this.enableMemo = false;
		}
		
		@Override
		public boolean simpleMatch(ParsingContext context) {
			return this.matchRef.simpleMatch(context);
		}
	}

}


final class MemoEntry {
	long key;
	ParsingObject result;
	int  consumed;
	int  memoPoint;
	MemoEntry next;
}

abstract class ParsingMemo {
	protected final static int FifoSize = 64;
	long AssuredLength = Integer.MAX_VALUE;

	int MemoHit = 0;
	int MemoMiss = 0;
	int MemoSize = 0;
//	int statMemoSlotCount = 0;


	private MemoEntry UnusedMemo = null;

	protected final MemoEntry newMemo() {
		if(UnusedMemo != null) {
			MemoEntry m = this.UnusedMemo;
			this.UnusedMemo = m.next;
			return m;
		}
		else {
			MemoEntry m = new MemoEntry();
//			this.memoSize += 1;
			return m;
		}
	}
	
	protected final void unusedMemo(MemoEntry m) {
		this.appendMemo2(m, UnusedMemo);
		UnusedMemo = m;
	}

	protected final MemoEntry findTail(MemoEntry m) {
		while(m.next != null) {
			m = m.next;
		}
		return m;
	}			

	private void appendMemo2(MemoEntry m, MemoEntry n) {
		while(m.next != null) {
			m = m.next;
		}
		m.next = n;
	}			

	protected abstract void setMemo(long pos, int memoPoint, ParsingObject result, int consumed);
	protected abstract MemoEntry getMemo(long pos, int memoPoint);

	protected void stat(ParsingStatistics stat) {
		stat.setCount("MemoUsed", this.MemoHit);
		stat.setCount("MemoStored", this.MemoMiss);
		stat.setRatio("Used/Stored", this.MemoHit, this.MemoMiss);
	}
}

class NoParsingMemo extends ParsingMemo {
	@Override
	protected void setMemo(long pos, int memoPoint, ParsingObject result, int consumed) {
	}

	@Override
	protected MemoEntry getMemo(long pos, int memoPoint) {
		this.MemoMiss += 1;
		return null;
	}
}

class PackratParsingMemo extends ParsingMemo {
	protected Map<Long, MemoEntry> memoMap;
	protected PackratParsingMemo(Map<Long, MemoEntry> memoMap) {
		this.memoMap = memoMap;
	}
	PackratParsingMemo(int initSize) {
		this(new HashMap<Long, MemoEntry>(initSize));
	}
	@Override
	protected final void setMemo(long pos, int memoPoint, ParsingObject result, int consumed) {
		MemoEntry m = null;
		m = newMemo();
		m.memoPoint = memoPoint;
		m.result = result;
		m.consumed = consumed;
		m.next = this.memoMap.get(pos);
		this.memoMap.put(pos, m);
	}
	@Override
	protected final MemoEntry getMemo(long pos, int memoPoint) {
		MemoEntry m = this.memoMap.get(pos);
		while(m != null) {
			if(m.memoPoint == memoPoint) {
				this.MemoHit += 1;
				return m;
			}
			m = m.next;
		}
		this.MemoMiss += 1;
		return m;
	}
}

class FifoMemo extends ParsingMemo {
	protected Map<Long, MemoEntry> memoMap;
	protected long farpos = 0;
	
	protected FifoMemo(int slot) {
		this.memoMap = new LinkedHashMap<Long, MemoEntry>(slot) {  //FIFO
			private static final long serialVersionUID = 6725894996600788028L;
			@Override
			protected boolean removeEldestEntry(Map.Entry<Long, MemoEntry> eldest)  {
				long pos = ParsingUtils.getpos(eldest.getKey());
				//System.out.println("diff="+(farpos - pos));
				if(farpos - pos > 256) {
					unusedMemo(eldest.getValue());
					return true;		
				}
				return false;
			}
		};
	}

	@Override
	protected final void setMemo(long pos, int memoPoint, ParsingObject result, int consumed) {
		MemoEntry m = null;
		m = newMemo();
		long key = ParsingUtils.objectId(pos, (short)memoPoint);
		m.key = key;
		m.memoPoint = memoPoint;
		m.result = result;
		m.consumed = consumed;
		this.memoMap.put(key, m);
		if(pos > this.farpos) {
			this.farpos = pos;
		}
	}

	@Override
	protected final MemoEntry getMemo(long pos, int memoPoint) {
		MemoEntry m = this.memoMap.get(ParsingUtils.objectId(pos, (short)memoPoint));
		if(m != null) {
			this.MemoHit += 1;
		}
		else {
			this.MemoMiss += 1;
		}
		return m;
	}
}

class FifoPackratParsingMemo extends ParsingMemo {
	private MemoEntry[] memoArray;
	private long statSetCount = 0;
	private long statExpireCount = 0;

	FifoPackratParsingMemo(int slotSize) {
		this.memoArray = new MemoEntry[slotSize];
		for(int i = 0; i < this.memoArray.length; i++) {
			this.memoArray[i] = new MemoEntry();
		}
	}
	
	@Override
	protected final void setMemo(long pos, int memoPoint, ParsingObject result, int consumed) {
		int index = (int)(pos % memoArray.length);
		long key = pos;
		MemoEntry m = this.memoArray[index];
		if(m.key != key) {
			m.key = key;
			m.memoPoint = memoPoint;
			m.result = result;
			m.consumed = consumed;
			if(m.next != null) {
				this.unusedMemo(m.next);
				m.next = null;
			}
		}
		else {
			MemoEntry m2 = newMemo();
			m2.key = key;
			m2.memoPoint = memoPoint;
			m2.result = result;
			m2.consumed = consumed;
			m.next = m2;
		}
	}

	@Override
	protected final MemoEntry getMemo(long pos, int memoPoint) {
		int index = (int)(pos % memoArray.length);
		long key = pos;
		MemoEntry m = this.memoArray[index];
		if(m.key == key) {
			while(m != null) {
				if(m.memoPoint == memoPoint) {
					this.MemoHit += 1;
					return m;
				}
				m = m.next;
			}
		}
		this.MemoMiss += 1;
		return null;
	}

	@Override
	protected final void stat(ParsingStatistics stat) {
		super.stat(stat);
		stat.setCount("MemoSize", this.memoArray.length);
		stat.setRatio("MemoCollision80", this.statExpireCount, this.statSetCount);
	}
}


class TracingPackratParsingMemo extends ParsingMemo {
	private MemoEntry[] memoArray;

	TracingPackratParsingMemo(int distance, int rules) {
		this.memoArray = new MemoEntry[distance * rules + 1];
		for(int i = 0; i < this.memoArray.length; i++) {
			this.memoArray[i] = new MemoEntry();
			this.memoArray[i].key = -1;
		}
	}
	
	@Override
	protected final void setMemo(long pos, int memoPoint, ParsingObject result, int consumed) {
		long key = ParsingUtils.objectId(pos, (short)memoPoint);
		int hash =  (Math.abs((int)key) % memoArray.length);
		MemoEntry m = this.memoArray[hash];
//		if(m.key != 0) {
//			long diff = keypos - PEGUtils.getpos(m.key);
//			if(diff > 0 && diff < 80) {
//				this.statExpireCount += 1;
//			}
//		}
		m.key = key;
		m.memoPoint = memoPoint;
		m.result = result;
		m.consumed = consumed;
	}

	@Override
	protected final MemoEntry getMemo(long pos, int memoPoint) {
		long key = ParsingUtils.objectId(pos, (short)memoPoint);
		int hash =  (Math.abs((int)key) % memoArray.length);
		MemoEntry m = this.memoArray[hash];
		if(m.key == key) {
			//System.out.println("GET " + key + "/"+ hash + " kp: " + keypeg.uniqueId);
			this.MemoHit += 1;
			return m;
		}
		this.MemoMiss += 1;
		return null;
	}

	@Override
	protected final void stat(ParsingStatistics stat) {
		super.stat(stat);
		stat.setCount("MemoSize", this.memoArray.length);
	}
}

class DebugMemo extends ParsingMemo {
	ParsingMemo m1;
	ParsingMemo m2;
	protected DebugMemo(ParsingMemo m1, ParsingMemo m2) {
		this.m1 = m1;
		this.m2 = m2;
	}
	@Override
	protected final void setMemo(long pos, int memoPoint, ParsingObject result, int consumed) {
		this.m1.setMemo(pos, memoPoint, result, consumed);
		this.m2.setMemo(pos, memoPoint, result, consumed);
	}
	@Override
	protected final MemoEntry getMemo(long pos, int memoPoint) {
		MemoEntry o1 = this.m1.getMemo(pos, memoPoint);
		MemoEntry o2 = this.m2.getMemo(pos, memoPoint);
		if(o1 == null && o2 == null) {
			return null;
		}
		if(o1 != null && o2 == null) {
			System.out.println("diff: 1 null " + "pos=" + pos + ", e=" + memoPoint);
		}
		if(o1 == null && o2 != null) {
			System.out.println("diff: 2 null " + "pos=" + pos + ", e=" + memoPoint);
		}
		if(o1 != null && o2 != null) {
			if(o1.result != o2.result) {
				System.out.println("diff: generaetd " + "pos1=" + pos + ", p1=" + memoPoint);
			}
			if(o1.consumed != o2.consumed) {
				System.out.println("diff: consumed " + "pos1=" + pos + ", p1=" + memoPoint);
			}
		}
		return o1;
	}
}
