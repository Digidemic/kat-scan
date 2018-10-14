# KatScan

### Write timestamped logs, exceptions, stack traces, or any form of text to a file for Android debugging purposes.
- Simple single parameter one liners to write stack traces or messages to user defined directories/files.
- Released apps and APKs automatically disable KatScan (no need to comment out or delete for release).
- Single file, `Kat.java`, that can be copied/pasted directly into an Android project or import the `katscan.aar` to use.
- Ready to use out of the box and fully configurable for customization.
- Compatible with every Android version API 4 (Android 1.6, Donut) and higher.
<br><br>
![](images/example1.png)

## Table of Contents
- [Examples](#examples)
- [Installation](#installation)
- [Syntax](#syntax)
- [Versioning](#versioning)
- [License](#license)

## Examples
- The results of running this sample code can be seen in the screenshot below.
The following briefly demonstrates the setup and type of calls that can be made using KatScan. 
- This sample code is also found and can be ran in this repo's example project, `/KatScan-ExampleApp/` using Android Studio.
```java
//PERMISSION ADDED TO MANIFEST: <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />

//Only pre-req call needed and only called once for the entirety of the application life!
Kat.setup(this, true);//Context and if should write to

//Pass in variables, exceptions, custom messages, or any form of text for KatScan to output to a separate file.
Kat.scan("Setup complete, This custom message written and time stamped to default file/directory:" +
        "/storage/emulated/0/KatScan_com.digidemic.katscanexamples/2018-09-30/KatScan_log.txt");
try{
    //Explicitly throw an error for this example
    String arrayOutOfBounds = new String[]{"KatScan", "error", "example"}[3];

} catch(Exception exception){

    //Pass in exception to have its full stack trace written to its default file.
    Kat.scan(exception);

    //Or have the exception stack trace be prefixed with a custom message.
    Kat.scan(exception, "Exception stack trace with this custom message written to default file.");

    //Or even have everything written to a new directory & file KatScan creates if does not already exist
    Kat.scan("/Errors/log", exception, "Exception stack trace with this custom message written to new subdirectory and file.");

}

//Dozens of configurable settings that can be updated at run-time
Kat.Config.File.lineBreakBetweenEachEntry = true;
Kat.scan("Every Kat.scan() going forward will have a line break between entries");
Kat.Config.createNewThreadForEachKatScanCall = true;
Kat.scan("This custom message was written using a new thread.");
```
![](images/example2.png)


## Installation
>Clone the KatScan repo to your local machine using `https://github.com/digidemic/KatScan`

### Install by importing katscan.aar (Recommended)
- #### Steps using Android Studio 3.1
>1) Download the katscan.aar (17.9kb as of v1.0.0 file from the KatScan repository.
>2) Launch Android Studio and open the project you wish to add KatScan to.
>3) Expand the `Project` tab the switch the folder structure to `Android`
>4) Find the root most node which in many cases is `app` (This contains the manifest, java, and res directory).
>5) Open the `Project Structure` window using one of two methods: Click to highlight the `app` directory > Press `F4` or right-click the `app` directory > Click `Open Module Settings`.
>6) In the `Project Structure` window, press the green plus button > Scroll and select `Import .JAR/.AAR Package` > Click `Next`.
>7) In the `File name` input box enter the full path of `katscan.aar` from your local machine or click the `...` button to the right of the input box then find and select the `katscan.aar` and press `Finish`.
>8) Click `OK` from within the `Project Structure` with `katscan` added to the `Modules` list.
>9) Expand the `Gradle Scripts` node in the `Project` tab > Locate and select the build.gradle file for `Module: app` (There may be several `build.gradle` files, make sure to find the one with your root node name which is `app` in most cases).
>10) Under the dependencies collection, add the following in a new line: `compile project(path: ':katscan')`
>11) Sync Gradle (A bar at the top of your code should appear giving a link to `Sync Now`).
>12) Your Android project is now ready to use KatScan! Go to the [Syntax](#syntax) section for the next steps on using KatScan in your application!

### Install by Copy/Pasting Kat.java
>1) Choose to download either `Kat.java` or `Kat_(NoJavadoc).java` (42.9kb and 29.2kb respectively as of v1.0.0) file from the KatScan repository.
Note: `Kat_(NoJavadoc).java` only differs from `Kat.java` by having all its comments and Javadocs removed making the file smaller. Functionality and code-wize it is exactly the same.
>2) Copy the file directly into a valid path of your project (preferably a place where Java files are commonly accessed).
>3) Open the file and update the package name (first line of code) to properly reflect your project's package.
>4) Add the following permission to your manifest: `<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />`
>5) Your Android project is now ready to use KatScan! Go to the [Syntax](#syntax) section for the next steps on using KatScan in your application!

## Syntax

### Setup / Pre-req call

#### Permissions
>The following permission needs to be added to your project's `Manifest.xml` if not using `katscan.aar`. This permission is needed for KatScan to write to external files.
```xml
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
```

#### Import
> Each class using KatScan needs the following import if using `katscan.aar`. If `Kat.java` was copied/pasted into your project instead, the import needs to be the full package name of your defined `Kat.java`.
```java
import com.digidemic.katscan.Kat;
```

#### Setup call
> `Kat.Setup()` is the only pre-req call needed and only called once for the entirety of the application's lifespan. There are 2 overloaded `Kat.Setup()` methods to choose from: One that requires just the class' `Context` to be passed in, the other for the `Context` and to set config `addEntriesIntoSubdirectoryCreatedToday`. Either one of the two (never both) needs to be called before the first `Kat.scan()` call anywhere Context (Or Activity) can be retrieved in your application. Consider passing in an active Activity in place of Context as devices using API 23 (Android Marshmallow) and higher will need to allow the "Storage" permission via popup. Because of this, it is highly recommended to add your `Kat.setup()` call in your main activity's `onCreate()` method passing in `this` for the Context.

>For the second parameter, Many would prefer having a main directory sub-folder with all of the current day's `Kat.scan()` entries. To quickly enable this, use the `Kat.Setup()` method using two parameters passing the second parameter as `true`. This can also be turned on by setting `Kat.Config.File.addEntriesIntoSubdirectoryCreatedToday = true`.
```java
/**
 * Option 1 of 2 for possible setup methods
 * Only needs to be called once in the entire lifespan of the application (recommended to be called in the project's main activity onCreate method).
 * @param context Context instance from the application to determine the application's package name and if the application version is in debug. Activity must be passed in to request the "WRITE_EXTERNAL_STORAGE" to be granted for devices API 23 and higher.
 * @return true if successfully called without error
 */
 Kat.setup(this);

 //OR

/**
 * Option 2 of 2 for possible setup methods
 * Only needs to be called once in the entire lifespan of the application (recommended to be called in the project's main activity onCreate method).
 * @param context Context instance from the application to determine the application's package name and if the application version is in debug. Activity must be passed in to request the "WRITE_EXTERNAL_STORAGE" to be granted for devices API 23 and higher.
 * @param addEntriesIntoSubdirectoryCreatedToday If true will add all entries created from Kat.Scan into a subfolder of the current day. Ex: "/storage/emulated/0/KatScan_com.digidemic.katscanexamples/2018-09-30/KatScan_log.txt" rather than "/storage/emulated/0/KatScan_com.digidemic.katscanexamples/KatScan_log.txt"
 * @return true if successfully called without error
 */
 Kat.setup(this, true);
```

### Main Syntax / Using Kat.scan()
>The main usage of KatScan is by calling `Kat.scan()` methods. There are 6 overloaded methods here allowing each call to write a message, exception stack track, or combination of the two to an external file (KatScan default or user defined file/directory).
```java
try{
    //Some code that throws and Exception
} catch(Exception exception) {
    /*
    Kat.scan() which writes to KatScan default file
     */
    Kat.scan("This String outputted to default file/path: /storage/emulated/0/KatScan_YOUR.PACKAGE.HERE/KatScan_log.txt");
    Kat.scan(exception); //Just Exception stack trace outputted to default file/path
    Kat.scan(exception, "Message to prefix the Exception stack trace outputted to default file/path");

    /*
    Kat.scan() which writes to user defined Directory/Files (which will be created at run-time if don't exist)
     */
    Kat.scan("/NewFolder/NewTxtFile", "This String outputted to user defined path: /storage/emulated/0/KatScan_YOUR.PACKAGE.HERE/NewFolder/NewFile.txt");
    Kat.scan("NewFileInMainDirectory", exception);
    Kat.scan("ExceptionFile", exception, "Message to prefix the Exception stack trace outputted to user defined file");
}
```

### Config
`Kat.Config` has all the configurable settings that can be updated at run-time. The following are all public configuration settings set to their default value.
```java
/*
Kat.Config.File
 */
Kat.Config.File.rootDirectoryPath = Environment.getExternalStorageDirectory() + "/";   //The absolute path just before the "mainDirectoryName" which stores all Kat.scan() entries for this project.
Kat.Config.File.mainDirectoryName = "KatScan_com.digidemic.katscan";   //NOTE: THIS IS REASSIGNED WHEN Kat.setup() IS CALLED. | The directory name that is created at the very end of the "rootDirectoryPath" which stores all KatScan entries for this project.
Kat.Config.File.defaultFileName = "KatScan_log";                //The name of the default file that Kat.scan() entries are written to if the developer does not pass in their own file/directory to write to instead.
Kat.Config.File.fileExtension = ".txt";                         //The file extension for all Kat.scan() files. This includes all instances including default file and user passed in files.
Kat.Config.File.lineBreakBetweenEachEntry = false;              //Add a line break between each Kat.scan() entry to the affected file.
Kat.Config.File.writeKatScanEntriesToFileInsteadOfLog = true;   //Each Kat.scan() call will be written to an external file on the user's device instead of writing to the console.
Kat.Config.File.writeCountWithEveryEntry = false;               //For the lifespan of the running application, each Kat.scan() call increments an internal value by 1 starting with 0. Include this value in the written entry output.
Kat.Config.File.addEntriesIntoSubdirectoryCreatedToday = false; //NOTE: AN OVERLOADED Kat.setup() METHOD CAN BE CALLED TO ASSIGN THIS VALUE TRUE. | To have each Kat.scan() entry written into a subfolder of the current day within "mainDirectoryName".

/*
Kat.Config.Date
 */
Kat.Config.Date.includePrefixedDateForEachFileEntry = true;   //Each Kat.scan() call is prefixed with the current date/time when written to the file.
Kat.Config.Date.entryDateFormatPattern = "yy-MM-dd_HH:mm:ss"; //The date format pattern used when "includePrefixedDateForEachFileEntry" is set to true.
Kat.Config.Date.subdirectoryDateFormatPattern = "yyyy-MM-dd"; //The date format pattern used when "addEntriesIntoSubdirectoryCreatedToday" is set to true.
Kat.Config.Date.includePrefixedDateForEachLogEntry = false;   //Each KatScan error or Kat.scan() meant to be written to the log instead of a file should have the date/time prefixed to its log entry.

/*
Kat.Config.InternalErrors
 */
Kat.Config.InternalErrors.showPermissionGrantedErrorIfOccurs = true;                        //If this error has not yet occurred or has not been displayed to the user's console yet.
Kat.Config.InternalErrors.showKatScanInternallyCaughtErrors = true;                         //If this error has not yet occurred or has not been displayed to the user's console yet.
Kat.Config.InternalErrors.showTheSetupErrorAsLogOnceIfNeededRegardlessIfDebugging = true;   //If this error has not yet occurred or has not been displayed to the user's console yet.
Kat.Config.InternalErrors.showKatScanInternallyCaughtErrorsAsLogsOverPrintln = true;        //If this error has not yet occurred or has not been displayed to the user's console yet.
Kat.Config.InternalErrors.showKatScanTextInLogsWhenFailureToWriteInFile = true;             //If this error has not yet occurred or has not been displayed to the user's console yet.
Kat.Config.InternalErrors.loggingMethod = Kat.Config.InternalErrors.LOG_METHOD.DEBUG;       //When anything needs to be logged, whether it be from a KatScan error or a Kat.scan() entry meant for the log, this is the defining log method.
Kat.Config.InternalErrors.logTag = "com.digidemic.katscan_entry";                           //The log tag of each log performed in the console.
Kat.Config.InternalErrors.LOG_METHOD = { ERROR, WARNING, INFORMATION, DEBUG, VERBOSE };     //NOTE: THIS IS AN ENUM. | This is used to assign Kat.Config.InternalErrors.loggingMethod

/*
Kat.Config.PermissionRequest
 */
Kat.Config.PermissionRequest.codeID = 65496;                                //The request code used when requesting KatScan needed permissions dialog for devices API 23 and higher.
Kat.Config.PermissionRequest.millisecondsPerAcceptPermissionsCheck = 1000;  //When showing the request permissions dialog a callback listener was not defined solely to not interfere with the main application. A timer is used in its place to determine if the permission has been accepted during the time to popup is on screen. This variable is how frequently the timer should check if the permission has been accepted.
Kat.Config.PermissionRequest.maxWaitTimeForUserToAcceptPermissions = 60000; //When showing the request permissions dialog a callback listener was not defined solely to not interfere with the main application. A timer is used in its place to determine if the permission has been accepted during the time to popup is on screen.  This variable is the max duration the timer will check for before ending and assuming the permission has been revoked.

/*
Kat.Config
 */
Kat.Config.spaceSeparator = " - ";                      //Spacing put in between entry date and message.
Kat.Config.createNewThreadForEachKatScanCall = false;   //Create a new thread for each Kat.scan() call.
Kat.Config.hasKatScanBeenEnabledRegardlessIfRunningInDebug();   //If the "enableKatScanRegardlessIfRunningInDebug" variable has manually been set to true
Kat.Config.isApplicationRunningInDebugMode();           //If KatScan detected that the application is running in debug mode, not release mode
Kat.Config.isKatScanEnabled();                          //KatScan is enabled either by "applicationRunningInDebug" being true or "enableKatScanRegardlessIfRunningInDebug" being true

/**
 * Allows the application to enable KatScan to write entries regardless if debug or release, useful for applications released and want a way to manually enable KatScan through the application
 * @param enable enable KatScan regardless of if debug or release
 */
Kat.Config.enableKatScanRegardlessIfRunningInDebug(boolean enable);

/**
 * Allows the application to enable KatScan to write entries regardless if debug or release, useful for applications released and want a way to manually enable KatScan through the application
 * @param enable enable KatScan regardless of if debug or release
 * @param activity pass in the application's Activity instance if device needs to grant Storage permissions still.
 */
Kat.Config.enableKatScanRegardlessIfRunningInDebug(boolean enable, Activity activity);
```


## Versioning
- [SemVer](http://semver.org/) is used for versioning.
- Given a version number MAJOR . MINOR . PATCH
    1) MAJOR version - Incompatible API changes.
    2) MINOR version - Functionality added in a backwards-compatible manner.
    3) PATCH version - Backwards-compatible bug fixes.

## Author
KatScan created by Adam Steinberg of DIGIDEMIC, LLC

## License
- KatScan licensed under **[Apache License 2.0](http://www.apache.org/licenses/LICENSE-2.0)**
- Copyright 2018 © <a href="http://digidemicsoftware.com/">DIGIDEMIC, LLC</a>.
