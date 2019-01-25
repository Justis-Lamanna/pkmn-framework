# Hex Framework [![Build Status](https://travis-ci.org/Justis-Lamanna/pkmn-framework.svg?branch=master)](https://travis-ci.org/Justis-Lamanna/pkmn-framework)

A rudimentary framework for dealing with hex files in an easier manner. 

* Allow easy parsing of complex objects using annotations and reflection, rather than reading the hex byte-by-byte. 
* Allow easy parsing and injecting of ini files
* Image manipulation
* Maybe other stuff probably over time.

End goal is to make a library that allows a programmer to pick up and start developing tools for ROM hacking, without
having to worry about rewriting a framework for reading bytes and parsing out objects each time.

Intended to be lightweight. Uses only apache commons (Lang) as a 3rd-party library.

Each sub-library in this library is meant to address a particular scope. The coreframework module is meant to hold
logic relevant to all hex files, such as pointers and bytes. The gbaframework module is meant to hold specific
logic for GBA games, such as GBA style pointers and graphics. Later in the project, a pkmngbaframework will be created
to hold logic specifically for Pokemon games, such as overworld sprite data.

## Quickstart
```java
HexFramework framework = HexFramework.init("path to file")
    .addHexer(GBAPointer.class, GBAPointer.HEXER)
    .addHexer(UnsignedByte.class, UnsignedByte.HEXER)
    .build()
    .orThrow();

//Reads a GBA Style pointer at 0x800000.
GBAPointer pointer = framework.read(0x800000, GBAPointer.class);

//Writes 0xFF to the pointer read above
framework.write(pointer, UnsignedByte.valueOf(0x0FF);
```

## Slightly Slower Start
### HexFramework
The HexFramework is the main class from which all operations should begin. You can begin creating one using the static method
`HexFramework.init("filename")`. Strictly speaking, no further configuration is necessary. However, you can register
Hexer-class associations, a framework factory which initializes certain presets, or other more advanced configuration detail
(see the docs)

### Hexers
After the call to `init`, we register two Hexers. Hexers are interfaces that read byte values, and convert them
into objects, and vice versa. We associate each Hexer with the class it reads, so that hexer can later be referenced
just from a class. All of the standard objects defined have an associated hexer. Many, like UnsignedByte and GBAPointer, have
a constant called HEXER defined for them. Others, such as GBASprite, have a static method to construct a Hexer based on
parameters such as sprite and bit depth.

It is impractical, in larger projects, to associate a Hexer for each object you plan to use. By replacing the above code with:
```java
HexFramework framework = HexFramework.init("path to file")
    .frameworkFactory(new GBAFrameworkFactory())
    .build()
    .orThrow();
```
We use a *FrameworkFactory* rather than registering hexers ourselves. A FrameworkFactory sets the Framework to presets,
which includes registering Hexers or custom pipelines. By registering GBAFrameworkFactory in this case, we now have access
to a pipeline specifically for GBA objects, and hexers for UnsignedByte, UnsignedShort, UnsignedWord, and GBAPointer.

Note that you don't need a Hexer for every object. We will discuss this in the section on read and write.

### Try
After configuring the framework, calling build will attempt to assemble one. This method returns a Try<HexFramework>,
which either returns an object wrapping the created HexFramework, or an error state. The `orThrow()` method, when called
on an error Try, simply throws a RuntimeException, with the error information. Otherwise, the containing object is unwrapped.

Errors can be handled another way, by calling one of the methods on the created try. A custom exception can be thrown, or
functions can operate on the wrapped object, conditionally.

Try is a bit of an anomaly within the framework, inspired from Rust. It attempts to reduce the number of exceptions that
are thrown, because I hate Try/Catch blocks, especially if they are checked exceptions. It is similar to `Optional`s, but
can contain null elements if necessary, and conveys a different meaning.

### Read
With a completed Framework, we can now read and write objects. To read an object, simply call `read()` on the framework,
and pass the pointer to the object, and a Hexer to use to parse the object from bytes. You can also pass the class of the object
to create, if you registered a Hexer to it during initialization.

### Write
To read an object, simply call `write()` on the framework,
and pass the pointer to the object, and a Hexer to use to parse the object into bytes. You can also pass the class of the object
to create, if you registered a Hexer to it during initialization.

### More Complex Objects
Data structures can also be created, by using Java annotations. Here is an example:
```java
@DataStructure
public class TestStructure {
    @Offset("0x0")
    UnsignedByte byteAt0;
    
    @Offset("0x4")
    GBAPointer pointerAt4;
}
```
The annotations describe the object's expected layout. The @DataStructure annotation marks this object as "friendly
for reflection-based parsing". It is required. When an object of TestStructure.class is requested through
`HexFramework.read()`, each field is parsed according to its @Offset annotation. In this case, `byteAt0` is an unsigned
byte at relative position 0, and `pointerAt4` is a GBA-Style Pointer at relative position 4.

Methods in a DataStructure may also be annotated with `@AfterRead` and `@BeforeWrite`, respectively. These are expected to be zero-argument
functions, which do modification to prepare the object for use. 

Last, consider this object:
```java
@DataStructure
public class TestStructure {
    @Offset("0x800000")
    @Absolute
    @PointerField(objectType = UnsignedByte.class)
    PointerObject<UnsignedByte> poAt800000;
}
```
This contains a few new annotations, and introduces PointerObjects. A PointerObject encapsulates a pointer, and
the object that pointer points to. In this case, we are retrieving the pointer located at 0x800000, and then
retrieving the UnsignedByte at that pointer (Note the @Absolute annotation, which says that this object is 
explicitly at 0x800000, not 0x800000 bytes relative to the read or write). The PointerField annotation
is required, because the type contained in PointerObject is erased during runtime.

### RepointStrategy
PointerObjects contain another field as well, called a RepointStrategy. This describes how an object should be
repointed when written. By default, PointerObjects are constructed with a `NoRepointStrategy`, which throws an exception
when a repoint is attempted (making the object read-only). `RepointUtils` contains methods for specifying other `RepointStrategy`s.
Right now, there is only the `identityReportStrategy()`, which writes the object exactly where it was. There are plans for
a RepointStrategy that will place the object in found free space.

The main intention of RepointStrategy is to allow the programmer to open a popup box, where the ROM hacker can enter
their own new pointer. This is implementation-specific, so is up to the programmer to write.

### Configuration Files
During initialization, you have the ability to set a Configuration. This is, in essence, a dictionary of constants that
the framework can use. For example, if the you have a Configuration with a key of "testKey", and a value of "0x800000",
the following could be used:
```java
@DataStructure
public class TestStructure {
    @Offset("${testKey|0x400000}")
    @Absolute
    @PointerField(objectType = UnsignedByte.class)
    PointerObject<UnsignedByte> poAt800000;
}
```
When a TestStructure is requested, "${testKey}" is read from the specified Configuration object. If testKey does
not exist in the configuration, the provided default of 0x400000 is used instead. The default may be omitted. Future
plans will allow the programmer to specify basic arithmetic operators in conjunction with the Configuration objects,
to specify, for example, `testKey + 4`.

The main intention of Configurations is to allow the use of ini files. Indeed, a file-based configuration is
the only one that currently exists out of the box, which can parse either a standard or XML Java Properties file.

### Pipelines and CreateStrategy
`Pipeline`s allow the programmer to customize the framework's process of reading or writing an object, when
not using a Hexer. Pipelines are made of pipes, which can be either `ReadPipe`s or `WritePipe`s. ReadPipes
are only invoked when an object is read from the file, and WritePipes are only invoked when an object is
written to the file. A `CreateStrategy` is used to specify how the object is created; by default, the no-arg
constructor of the specified class is invoked.

Pipelines can get rather complex, so I recommend looking into the `GBAFrameworkFactory` source, which defines
a pipeline to handle GBA-specific annotations @Sprite and @Palette, which are used to parse GBASprite and GBAPalette
objects, respectively.
