# CMAKE generated file: DO NOT EDIT!
# Generated by "Unix Makefiles" Generator, CMake Version 3.0

#=============================================================================
# Special targets provided by cmake.

# Disable implicit rules so canonical targets will work.
.SUFFIXES:

# Remove some rules from gmake that .SUFFIXES does not remove.
SUFFIXES =

.SUFFIXES: .hpux_make_needs_suffix_list

# Suppress display of executed commands.
$(VERBOSE).SILENT:

# A target that is always out of date.
cmake_force:
.PHONY : cmake_force

#=============================================================================
# Set environment variables for the build.

# The shell in which to execute make rules.
SHELL = /bin/sh

# The CMake executable.
CMAKE_COMMAND = /usr/local/Cellar/cmake/3.0.2/bin/cmake

# The command to remove a file.
RM = /usr/local/Cellar/cmake/3.0.2/bin/cmake -E remove -f

# Escaping for special characters.
EQUALS = =

# The top-level source directory on which CMake was run.
CMAKE_SOURCE_DIR = /Users/hondashun/Documents/workspace/peg4d-java/vm

# The top-level build directory on which CMake was run.
CMAKE_BINARY_DIR = /Users/hondashun/Documents/workspace/peg4d-java/vm/build

# Include any dependencies generated for this target.
include CMakeFiles/pegvm.dir/depend.make

# Include the progress variables for this target.
include CMakeFiles/pegvm.dir/progress.make

# Include the compile flags for this target's objects.
include CMakeFiles/pegvm.dir/flags.make

CMakeFiles/pegvm.dir/Users/hondashun/Documents/workspace/peg4d-java/libpeg4d/pegvm.c.o: CMakeFiles/pegvm.dir/flags.make
CMakeFiles/pegvm.dir/Users/hondashun/Documents/workspace/peg4d-java/libpeg4d/pegvm.c.o: /Users/hondashun/Documents/workspace/peg4d-java/libpeg4d/pegvm.c
	$(CMAKE_COMMAND) -E cmake_progress_report /Users/hondashun/Documents/workspace/peg4d-java/vm/build/CMakeFiles $(CMAKE_PROGRESS_1)
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green "Building C object CMakeFiles/pegvm.dir/Users/hondashun/Documents/workspace/peg4d-java/libpeg4d/pegvm.c.o"
	/usr/bin/cc  $(C_DEFINES) $(C_FLAGS) -o CMakeFiles/pegvm.dir/Users/hondashun/Documents/workspace/peg4d-java/libpeg4d/pegvm.c.o   -c /Users/hondashun/Documents/workspace/peg4d-java/libpeg4d/pegvm.c

CMakeFiles/pegvm.dir/Users/hondashun/Documents/workspace/peg4d-java/libpeg4d/pegvm.c.i: cmake_force
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green "Preprocessing C source to CMakeFiles/pegvm.dir/Users/hondashun/Documents/workspace/peg4d-java/libpeg4d/pegvm.c.i"
	/usr/bin/cc  $(C_DEFINES) $(C_FLAGS) -E /Users/hondashun/Documents/workspace/peg4d-java/libpeg4d/pegvm.c > CMakeFiles/pegvm.dir/Users/hondashun/Documents/workspace/peg4d-java/libpeg4d/pegvm.c.i

CMakeFiles/pegvm.dir/Users/hondashun/Documents/workspace/peg4d-java/libpeg4d/pegvm.c.s: cmake_force
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green "Compiling C source to assembly CMakeFiles/pegvm.dir/Users/hondashun/Documents/workspace/peg4d-java/libpeg4d/pegvm.c.s"
	/usr/bin/cc  $(C_DEFINES) $(C_FLAGS) -S /Users/hondashun/Documents/workspace/peg4d-java/libpeg4d/pegvm.c -o CMakeFiles/pegvm.dir/Users/hondashun/Documents/workspace/peg4d-java/libpeg4d/pegvm.c.s

CMakeFiles/pegvm.dir/Users/hondashun/Documents/workspace/peg4d-java/libpeg4d/pegvm.c.o.requires:
.PHONY : CMakeFiles/pegvm.dir/Users/hondashun/Documents/workspace/peg4d-java/libpeg4d/pegvm.c.o.requires

CMakeFiles/pegvm.dir/Users/hondashun/Documents/workspace/peg4d-java/libpeg4d/pegvm.c.o.provides: CMakeFiles/pegvm.dir/Users/hondashun/Documents/workspace/peg4d-java/libpeg4d/pegvm.c.o.requires
	$(MAKE) -f CMakeFiles/pegvm.dir/build.make CMakeFiles/pegvm.dir/Users/hondashun/Documents/workspace/peg4d-java/libpeg4d/pegvm.c.o.provides.build
.PHONY : CMakeFiles/pegvm.dir/Users/hondashun/Documents/workspace/peg4d-java/libpeg4d/pegvm.c.o.provides

CMakeFiles/pegvm.dir/Users/hondashun/Documents/workspace/peg4d-java/libpeg4d/pegvm.c.o.provides.build: CMakeFiles/pegvm.dir/Users/hondashun/Documents/workspace/peg4d-java/libpeg4d/pegvm.c.o

# Object files for target pegvm
pegvm_OBJECTS = \
"CMakeFiles/pegvm.dir/Users/hondashun/Documents/workspace/peg4d-java/libpeg4d/pegvm.c.o"

# External object files for target pegvm
pegvm_EXTERNAL_OBJECTS =

libpegvm.dylib: CMakeFiles/pegvm.dir/Users/hondashun/Documents/workspace/peg4d-java/libpeg4d/pegvm.c.o
libpegvm.dylib: CMakeFiles/pegvm.dir/build.make
libpegvm.dylib: CMakeFiles/pegvm.dir/link.txt
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --red --bold "Linking C shared library libpegvm.dylib"
	$(CMAKE_COMMAND) -E cmake_link_script CMakeFiles/pegvm.dir/link.txt --verbose=$(VERBOSE)

# Rule to build all files generated by this target.
CMakeFiles/pegvm.dir/build: libpegvm.dylib
.PHONY : CMakeFiles/pegvm.dir/build

CMakeFiles/pegvm.dir/requires: CMakeFiles/pegvm.dir/Users/hondashun/Documents/workspace/peg4d-java/libpeg4d/pegvm.c.o.requires
.PHONY : CMakeFiles/pegvm.dir/requires

CMakeFiles/pegvm.dir/clean:
	$(CMAKE_COMMAND) -P CMakeFiles/pegvm.dir/cmake_clean.cmake
.PHONY : CMakeFiles/pegvm.dir/clean

CMakeFiles/pegvm.dir/depend:
	cd /Users/hondashun/Documents/workspace/peg4d-java/vm/build && $(CMAKE_COMMAND) -E cmake_depends "Unix Makefiles" /Users/hondashun/Documents/workspace/peg4d-java/vm /Users/hondashun/Documents/workspace/peg4d-java/vm /Users/hondashun/Documents/workspace/peg4d-java/vm/build /Users/hondashun/Documents/workspace/peg4d-java/vm/build /Users/hondashun/Documents/workspace/peg4d-java/vm/build/CMakeFiles/pegvm.dir/DependInfo.cmake --color=$(COLOR)
.PHONY : CMakeFiles/pegvm.dir/depend

