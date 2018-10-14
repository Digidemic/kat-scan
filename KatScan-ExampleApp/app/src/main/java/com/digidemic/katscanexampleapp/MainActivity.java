/**
 * KatScan v1.0.0 - https://github.com/Digidemic/KatScan
 * (c) 2018 DIGIDEMIC, LLC - All Rights Reserved
 * KatScan developed by Adam Steinberg of DIGIDEMIC, LLC
 * License: Apache License 2.0
 *
 * ====
 *
 * Copyright 2018 DIGIDEMIC, LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.digidemic.katscanexampleapp;

import android.content.Context;
import android.os.Bundle;
import android.os.Environment;

/**
 * Runnable KatScan app and documentation.
 *
 * NOTE:
 * IF KatScan WAS IMPORTED INTO YOUR PROJECT ANY OTHER WAY THAN WITH THE .aar FILE
 * THE FOLLOWING PERMISSION NEEDS TO ADDED TO YOUR MANIFEST XML:
 * <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
 *
 * Table of contents in file:
 * -=-=-=-=-=-=-=-=-=-=-=-=-=-=-
 * runExample(context) - Standalone code that run if running this demo app.
 * katSetupAndPermissions() - Documentation of permissions and setup method needed to be called in application before calling Kat.scan()
 * basicSyntax() - Documentation of all overloaded Kat.scan() methods.
 * katConfig() - Documentation of all configurable parameters in KatScan
 * -=-=-=-=-=-=-=-=-=-=-=-=-=-=-
 *
 */
public class MainActivity extends MiscForRunningDemo {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    public static void runExample(Context context){
        //Only pre-req call needed and only called once for the entirety of the application life!
        Kat.setup(context, true);//Context and if should write to subdirectory of today's date

        //Pass in variables, exceptions, custom messages, or any form of text for KatScan to output to a separate file.
        Kat.scan("Setup complete, This custom message written and time stamped to default file/directory:" +
                "/storage/emulated/0/KatScan_YOUR.PACKAGE.HERE/yyyy-MM-dd/KatScan_log.txt");
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
        Kat.scan("Every Kat.scan() going forward will have a line break between entries.");
        Kat.Config.createNewThreadForEachKatScanCall = true;
        Kat.scan("This custom message was written using a new thread.");
    }

    /**
     * -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
     * -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
     * DOCUMENTATION METHOD: TO SHOWCASE AND DESCRIBE BOTH Kat.setup METHODS, NOT FOR RUNNING.
     * -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
     * -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
     */
    private void katSetupAndPermissions(){
        //Permissions:
        /**
         * Add this to your Manifest.xml if importing Kat.java or Copying/Pasting the code into your solution (.aar import does not need to do this):
         * <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
         */

        //Setup:
        /**
         * Kat.Setup is the only pre-req call needed and only called once for the entirety of the application lifespan.
         * There are 2 overloaded methods to choose from to call here, one that requires the Context to be passed in, the other for Context and to set "addEntriesIntoSubdirectoryCreatedToday".
         * Either one of the two (never both) should be called before the first Kat.scan anywhere Context (Or Activity) can be retrieved in your application.
         * It is highly recommended to this in your main activity's o method.
         * Consider passing in an active Activity in place of Context for device's using API 23 (Android Marshmallow) and higher to allow the "Allow permission" popup for "Storage" to appear when needed. Otherwise this setting can be toggled on in the device's app settings screen.
         *
         * For many, having a main directory subfolder with all of the current day's Kat.scan entries is preferred.
         * To quickly enable this use the Kat.Setup method passing 2 parameters where the second on, "addEntriesIntoSubdirectoryCreatedToday", can be set true.
         * This can also be toggled any time later by re-assigning Kat.Config.File.addEntriesIntoSubdirectoryCreatedToday
         */

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
    }


    /**
     * -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
     * -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
     * DOCUMENTATION METHOD: TO SHOWCASE AND DESCRIBE ALL Kat.scan() METHODS, NOT FOR RUNNING.
     * -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
     * -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
     */
    private void basicSyntax(){
        try{
            //Some code that throws and Exception
        } catch(Exception exception) {
            /*
            Kat.scan() which write to KatScan default file
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
    }


    /**
     * -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
     * -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
     * DOCUMENTATION METHOD: TO SHOWCASE AND DESCRIBE ALL PUBLIC CONFIGURATION SETTINGS SET TO THEIR DEFAULTS, NOT FOR RUNNING.
     * -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
     * -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
     */
    private void katConfig(){
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
        //Kat.Config.InternalErrors.LOG_METHOD = { ERROR, WARNING, INFORMATION, DEBUG, VERBOSE };   //NOTE: THIS IS AN ENUM. | This is used to assign Kat.Config.InternalErrors.loggingMethod

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
        Kat.Config.enableKatScanRegardlessIfRunningInDebug(true);

        /**
         * Allows the application to enable KatScan to write entries regardless if debug or release, useful for applications released and want a way to manually enable KatScan through the application
         * @param enable enable KatScan regardless of if debug or release
         * @param activity pass in the application's Activity instance if device needs to grant Storage permissions still.
         */
        Kat.Config.enableKatScanRegardlessIfRunningInDebug(true, this);
    }
}
