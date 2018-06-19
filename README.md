# SPL Compiler
Welcome to the GitHub Repository for the Simple Programming Language compiler.

## Installation
Navigate to [Releases](https://github.com/ScaleRunner/compiler_construction/releases) and download the latest compiler-X.X.jar.
You can use the jar by naivgating to the directory and running the following command:
```
java -jar compiler-X.X.jar -h
```
This will output the usage of the compiler.

```
usage: Compiler
 -c,--compile-only       Only compile the code, do not run it
 -h,--help               Show compiler usage
 -i,--input-file <arg>   The SPL filepath
 -p,--python             Compile to Python instead of SSM
 -r,--reformat           Reformat SPL code and exit (WARNING: Removes
                         comments)
```

### Python Installation
The python compilation uses custom data types for Lists, as it uses a LinkedList implementation, and tuples, as python tuples are immutable. If the python package is installed the will be imported, otherwise they will be appended to the generated output file when used.

To install the python package, download the latest version of the spl_types.zip from the [Releases](https://github.com/ScaleRunner/compiler_construction/releases). Unzip them to your specified folder and navigate to the environment. To install them, execute the following command:

#### Linux
```
sudo python3 setup.py install
```
#### Windows
```
python setup.py install
```
