/**
 * KatScan v1.1.0 - https://github.com/Digidemic/kat-scan
 * (c) 2024 DIGIDEMIC, LLC - All Rights Reserved
 * KatScan developed by Adam Steinberg of DIGIDEMIC, LLC
 * License: Apache License 2.0
 *
 * ====
 *
 * Copyright 2024 DIGIDEMIC, LLC
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

package com.digidemic.katscan;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.CountDownTimer;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Write timestamped logs, exceptions, stack traces, messages, and other forms of text to a file for Android debugging purposes.
 *
 *  - Perform simple one liners anywhere in the application passing in only the exception or message to write to the default file or developer specified file.
 *  - Released apps and APKs automatically disable KatScan from requesting extra permission and writting to or creating files unless otherwise specified by the developer.
 *  - Only one permission needs to be added (Do not need to add if using .aar) in the manifest: <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
 *  - Ready to use out of the box but fully configurable at run-time if needed.
 *  - Inner error handlers to ensure there are no possibilities of KatScan crashing the application.
 *  - Add the .aar library or simply copy and paste the single Kat.scan class file into yout project.
 *  - Direcotries and files defined are automatically created and updated when Kat.scan() is called
 *  - Compatible with every Android version 1.6 and above (Android Donut, API Level 4)
 *  - Write to logs still if you do not desire outputting to a file or want to grant the "Storage" permission.
 */
public class Kat {

    /*
    Default constants
     */
    private static final int DEFAULT_PERMISSION_REQUEST_CODE = 65496;
    private static final long DEFAULT_MILLI_PER_ACCEPT_PERMISSIONS_CHECK = 1000;
    private static final long DEFAULT_MAX_WAIT_TIME_FOR_USER_TO_ACCEPT_PERMISSIONS = 1 * 60 * 1000;
    private static final String WRITE_PERM = "WRITE_EXTERNAL_STORAGE";
    private static final String FULL_WRITE_PERM = "<uses-permission android:name=\"android.permission." + WRITE_PERM + "\" />";
    private static final String KATSCAN_ERROR_PREFIX = "KatScan: ";
    private static final String DIR_DELIMITER = "/";
    private static final String DEFAULT_PACKAGE_NAME = "com.digidemic.katscan";
    private static final String DEFAULT_LOG_TAG_NAME = DEFAULT_PACKAGE_NAME + "_entry";
    private static final String DEFAULT_ENTRY_DATE_FORMAT_PATTERN = "yy-MM-dd_HH:mm:ss";
    private static final String DEFAULT_SUB_DIRECTORY_DATE_FORMAT_PATTERN = "yyyy-MM-dd";
    private static final String DEFAULT_SPACE_SEPARATOR = " - ";
    private static final String DEFAULT_KATSCAN_NAME_UNDERSCORE = "KatScan_";
    private static final String DEFAULT_MAIN_DIRECTORY_NAME = DEFAULT_KATSCAN_NAME_UNDERSCORE + DEFAULT_PACKAGE_NAME;
    private static final String DEFAULT_FILE_NAME = DEFAULT_KATSCAN_NAME_UNDERSCORE + "log";
    private static final String DEFAULT_ROOT_DIRECTORY_PATH = (Environment.getExternalStorageDirectory() != null) ? Environment.getExternalStorageDirectory() + DIR_DELIMITER : DIR_DELIMITER;
    private static final String DEFAULT_FILE_EXTENSION = ".txt";

    /*
    Private variables
     */
    private static long entryCount = 0;
    private static boolean setupComplete = false;
    private static boolean properPermissionsGranted = false;
    private static boolean applicationRunningInDebug = false;
    private static boolean enableKatScanRegardlessIfRunningInDebug = false;
    private static boolean hasAlreadyAskedForPermission = false;
    private static boolean hasSetupIncompleteMessageDisplayed = false;
    private static boolean hasDeniedPermissionsMessageDisplayed = false;
    private static boolean hasRequestPermissionContextErrorDisplayed = false;
    private static boolean hasWriteStoragePermissionGrantedErrorMessageDisplayed = false;
    private static boolean hasInvalidPathMessageDisplayed = false;
    private static boolean userPermissionRequestFinished = false;
    private static long permissionRequestTimeCounter = 0;
    private static CountDownTimer permissionRequestTimer = null;

    /**
     * Setup only needs to be called once in the entire lifespan of the application and should be called before any Kat.Scan() call to initialize setup variables.
     * With this call, the method gets the application's package name, if in debug, and requests to the device for the "WRITE_EXTERNAL_STORAGE" permission if needed (only if application version is in debug and context must be an Activity instance to request permission for devices API 23 and higher).
     * This method can be skipped if planning to call Config.enableKatScanRegardlessIfRunningInDebug(true); to allow running in released version of application, package name does not matter for folder structure, and application does not need to request the "WRITE_EXTERNAL_STORAGE".
     *
     * NOTE:
     * If class was imported or copy/pasted into your project, add the following permission to your manifest: <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
     * If .aar library was imported into your project (like through JitPack) then this permission does not need to be added.
     *
     * @param context Context instance from the application to determine the application's package name and if the application version is in debug. Activity must be passed in to request the "WRITE_EXTERNAL_STORAGE" to be granted for devices API 23 and higher.
     * @return true if successfully called without error
     */
    public static boolean setup(Context context){
        try{
            if(!setupComplete && context != null) {
                applicationRunningInDebug = ((context.getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0); //If application running in debug
                requestStoragePermissionIfNotAlreadyGranted(context);                                               //If context is Activity and device is API 23 or higher the application needs to request "Write" permission to be granted if not already
                Config.File.mainDirectoryName = DEFAULT_KATSCAN_NAME_UNDERSCORE + getApplicationPackageName(context) + DIR_DELIMITER;   //Main directory name by default to be the current application's package name
                setupComplete = true;                                                                               //No errors thrown during setup, set this as complete
            } else if(setupComplete && context != null && !properPermissionsGranted){                               //If setup is called more than once and application permission was previously granted. Maybe first time .setup was called was from a non-Activity context like a startup broadcast receiver. With this condition, try to get the write permission granted again if in debug.
                requestStoragePermissionIfNotAlreadyGranted(context);
            }
        } catch(Exception e){
            Error.log(e);
        }
        return setupComplete;
    }

    /**
     * Write entry (full exception stack trace) into default log/file (KatScan_log.txt).
     * @param exception Exception variable from "catch" so to have its full stack trace written to the log/file.
     */
    public static void scan(Exception exception){ start(exception == null ? "null" : null, exception, null); }

    /**
     * Write entry (string, number, variable, or any form of text) into default log file (KatScan_log.txt).
     * @param message String, number, variable, or any form of text.
     */
    public static void scan(Object message){
        start(String.valueOf(message), null, null);
    }

    /**
     * Write entry (Full exception stack trace with a message in string, number, variable, or any form of text) into default log file (KatScan_log.txt).
     * @param exception Exception variable from "catch" so to have its full stack trace written to the log/file.
     * @param message String, number, variable, or any form of text.
     */
    public static void scan(Exception exception, Object message){ start(String.valueOf(message), exception, null); }

    /**
     * Write entry (full exception stack trace) into directory path/file passed in (/Error/MainActivityLog.txt).
     * @param addEntryToThisFileName Directory path/file to write entry to starting from the defined root path. Ex: "/Error/MainActivityLog.txt" passed may write to "/storage/emulated/0/KatScan_com.digidemic.katscanexamples/Error/MainActivityLog.txt".
     * @param exception Exception variable from "catch" so to have its full stack trace written to the log/file.
     */
    public static void scan(Object addEntryToThisFileName, Exception exception){
        start(exception == null ? "null" : null, exception, String.valueOf(addEntryToThisFileName));
    }

    /**
     * Write entry (string, number, variable, or any form of text) into directory path/file passed in (/Error/MainActivityLog.txt).
     * @param addEntryToThisFileName Directory path/file to write entry to starting from the defined root path. Ex: "/Error/MainActivityLog.txt" passed may write to "/storage/emulated/0/KatScan_com.digidemic.katscanexamples/Error/MainActivityLog.txt".
     * @param message String, number, variable, or any form of text.
     */
    public static void scan(Object addEntryToThisFileName, Object message){
        start(String.valueOf(message), null, String.valueOf(addEntryToThisFileName));
    }

    /**
     * Write entry (Full exception stack trace with a message in string, number, variable, or any form of text) into directory path/file passed in (/Error/MainActivityLog.txt).
     * @param addEntryToThisFileName Directory path/file to write entry to starting from the defined root path. Ex: "/Error/MainActivityLog.txt" passed may write to "/storage/emulated/0/KatScan_com.digidemic.katscanexamples/Error/MainActivityLog.txt".
     * @param exception Exception variable from "catch" so to have its full stack trace written to the log/file.
     * @param message String, number, variable, or any form of text.
     */
    public static void scan(Object addEntryToThisFileName, Exception exception, Object message){
        start(String.valueOf(message), exception, String.valueOf(addEntryToThisFileName));
    }

    private static void start(Object message, Exception exception, Object addEntryToThisFileName){
        try {
            if (Config.createNewThreadForEachKatScanCall) { //Write entry using a new thread
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        writeEntry(message, exception, addEntryToThisFileName);
                    }
                }).start();
            } else {                                        //Write entry not using a new thread
                writeEntry(message, exception, addEntryToThisFileName);
            }
        } catch(Exception e){
            Error.log(e);
        }
    }

    private static void writeEntry(Object message, Exception exception, Object addEntryToThisFileName){
        try {
            if (!setupComplete && !hasSetupIncompleteMessageDisplayed && Config.InternalErrors.showTheSetupErrorAsLogOnceIfNeeded) {
                hasSetupIncompleteMessageDisplayed = true;
                Error.log(KATSCAN_ERROR_PREFIX + "Kat.setup(context); has not been called properly and needs to only once for the lifespan of the application in order to work properly.\n" +
                        "\tInitial setup variables have not been reassigned meaning everything will be saved in their KatScan default paths.\n" +
                        "\tBecause of this, KatScan cannot determine if the application is running in debug or release mode. As a result, all Kat.scan() calls will not write to any files or logs until Kat.setup() is properly called (unless the debug override has been set true, Kat.Config.enableKatScanRegardlessIfRunningInDebug(true);\n" +
                        "\tPlease call Kat.setup(context); in the OnCreate() of your main activity or just once in your project before performing your first Kat.scan() call.\n" +
                        "\tKat.setup(context) can be called from other class instances like Services or Broadcast Receiver but will be unable to request " + WRITE_PERM + " permission if the device is API 23 or higher.\n");
            }
            if (katScanEnabled()) {
                if(Config.File.writeCountWithEveryEntry) {
                    entryCount = entryCount + 1;
                }

                String date = getEntryDate();
                String filePath = constructFilePath(addEntryToThisFileName);

                writeEntryToFileOrLog(constructEntryText(message, exception), filePath, date);

                if(Config.File.lineBreakBetweenEachEntry){
                    writeEntryToFileOrLog("", filePath, null);
                }
            }
        } catch(Exception e){
            Error.log(e);
        }
    }

    private static String constructEntryText(Object message, Exception exception){
        try{
            if(message != null || exception != null) {
                StringBuilder txt = new StringBuilder();
                if (message != null) {
                    txt.append(String.valueOf(message));
                }
                if (message != null && exception != null) {
                    txt.append("\n\t");
                }
                if (exception != null) {
                    txt.append(exceptionToString(exception));
                }
                return txt.toString();
            }
        } catch (Exception e){
            Error.log(e);
        }
        return null;
    }

    private static String getApplicationPackageName(Context context){
        try {
            String packageName = DEFAULT_PACKAGE_NAME;
            if (context != null && context.getClass() != null && context.getClass().getPackage() != null && context.getClass().getPackage().getName() != null) {
                packageName = context.getClass().getPackage().getName();
            }
            if (packageName == null || packageName.equals("")) {
                packageName = DEFAULT_PACKAGE_NAME;
            }
            return packageName;
        } catch(Exception e){
            Error.log(e);
            return DEFAULT_PACKAGE_NAME;
        }
    }

    private static String constructFilePath(Object addEntryToThisFileName){
        try {
            StringBuilder filePath = new StringBuilder();
            filePath.append(String.valueOf(Config.File.getFullPathToMainDirectory()));
            if(Config.File.addEntriesIntoSubdirectoryCreatedToday){
                filePath.append(getSubdirectoryDate()).append(DIR_DELIMITER);
            }
            filePath.append(String.valueOf((addEntryToThisFileName != null) ? addEntryToThisFileName : Config.File.defaultFileName));
            filePath.append(String.valueOf(Config.File.fileExtension));
            return filePath.toString();
        } catch(Exception e){
            Error.log(e);
            return null;
        }
    }

    private static void writeEntryToFileOrLog(Object txt, String filePath, String date){
        try {
            if(txt != null) {
                StringBuilder entry = new StringBuilder();
                if(date != null) {
                    entry.append(date);
                    if(Config.File.writeCountWithEveryEntry) {
                        entry.append(String.valueOf(Config.spaceSeparator)).append(entryCount);
                    }
                    entry.append(String.valueOf(Config.spaceSeparator)).append(String.valueOf(txt));
                } else {
                    entry.append(String.valueOf(txt));
                }
                boolean wroteEntryToFile = false;
                if(designatedWriteLocation(true) && properPermissionsGranted && filePath != null) {
                    if (Storage.createFile(filePath)) {
                        FileWriter fw = new FileWriter(filePath, true);
                        BufferedWriter bw = new BufferedWriter(fw);
                        PrintWriter out = new PrintWriter(bw);
                        out.println(entry.toString());
                        out.flush();

                        //Attempt to free up resources faster
                        fw.close();
                        bw.close();
                        out.close();
                        out = null;
                        bw = null;
                        fw = null;
                        wroteEntryToFile = true;
                    } else {
                        Error.log(KATSCAN_ERROR_PREFIX + "File could not be created or does not exist | filePath: " + filePath + " message: " + entry.toString());
                    }
                }
                if(designatedWriteLocation(false)) {
                    executeConsolePrint(entry.toString());
                }
                if(wroteEntryToFile) {
                    return;
                }
            }
        } catch (Exception e) {
            Error.log(e);
        }
        try {
            if (!properPermissionsGranted && !hasDeniedPermissionsMessageDisplayed && designatedWriteLocation(true)) {
                hasDeniedPermissionsMessageDisplayed = true;
                Error.log(KATSCAN_ERROR_PREFIX + "Required \"Storage\" permission has not been granted by the device.\n" +
                        "\tKatScan only requires 1 permission,\n" +
                        "\t" + FULL_WRITE_PERM + "\n" +
                        "\twhich needs to be added to the application's AndroidManifest.xml file.\n" +
                        "\tThis permission is needed to output Kat.scan() entries to a separate file.\n" +
                        "\tIf this permission has already been added to the project manifest, make sure Kat.setup() is called just once in your project before your first Kat.scan() call.\n" +
                        "\tIf on a device using API 23 or higher, this will trigger a popup asking the user to grant the \"Storage\" permission your app.\n" +
                        "\tIf the \"Never ask again\" checkbox was checked and the permission was revoked you can also enable this permission by going to the app settings on your device and enabling it there.\n");
            }
            if (txt != null && filePath == null && !hasInvalidPathMessageDisplayed && designatedWriteLocation(true)) {
                hasInvalidPathMessageDisplayed = true;
                Error.log(KATSCAN_ERROR_PREFIX + "File path is null so a file could not be written");
            }
            if (txt != null && (Config.InternalErrors.showKatScanTextInLogsWhenFailureToWriteInFile || designatedWriteLocation(false))) {
                if (!userPermissionRequestFinished && designatedWriteLocation(true)) {
                    Error.log(KATSCAN_ERROR_PREFIX + "Currently requesting user to grant permission to use the " + WRITE_PERM + ".\n" +
                            "\tBecause the following Kat.scan entry has been called during requesting time it will only be displaying in the log.");
                }
            }
        } catch(Exception e){
            Error.log(e);
        }
    }

    private static String exceptionToString(Exception exception){
        try{
            if(exception != null){
                StringWriter sw = new StringWriter();
                exception.printStackTrace(new PrintWriter(sw));
                String exceptionStr = sw.toString();

                //Attempt to free up resources faster
                sw.close();
                sw = null;
                exception = null;
                sw = null;
                if(exceptionStr != null && exceptionStr.endsWith("\n")){    //Remove the extra line break at the end of the exception as it messes with the formatting
                    return exceptionStr.substring(0, exceptionStr.length() - 1);
                }
                return exceptionStr;
            }
        } catch(Exception e){
            Error.log(e.getMessage());  //Message passed in instead of error as will cause a stack overflow if exception is passed in.
        }
        return "";
    }

    private static String getEntryDate(){
        try {
            if (Config.Date.includePrefixedDateForEachFileEntry) {
                return new SimpleDateFormat(String.valueOf(Config.Date.entryDateFormatPattern)).format(Calendar.getInstance().getTime());
            }
        } catch (Exception e){
            Error.log(e);
        }
        return "";
    }

    private static String getSubdirectoryDate(){
        try {
            if (Config.File.addEntriesIntoSubdirectoryCreatedToday) {
                return new SimpleDateFormat(String.valueOf(Config.Date.subdirectoryDateFormatPattern)).format(Calendar.getInstance().getTime());
            }
        } catch (Exception e){
            Error.log(e);
        }
        return "";
    }

    private static boolean requestStoragePermissionIfNotAlreadyGranted(Context context) {
        try {
            if (isWriteStoragePermissionGranted(context)) {
                return true;
            } else if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M && katScanEnabled() && !hasAlreadyAskedForPermission && designatedWriteLocation(true)) {
                if (!hasRequestPermissionContextErrorDisplayed && (context == null || !(context instanceof Activity))) {
                    hasRequestPermissionContextErrorDisplayed = true;
                    Error.log(KATSCAN_ERROR_PREFIX + "Required \"Storage\" permission has not been granted by the device and cannot be requested with the \"context\" passed in.\n" +
                            "\tAn active Activity needs to be passed in to Kat.setup() to request the " + WRITE_PERM + " permission which is needed to output Kat.scan() entries to a file.\n" +
                            "\tAlso please make sure the following permission has bee added to your project's manifest xml:\n" +
                            "\t" + FULL_WRITE_PERM + "\n");
                }
                if (context != null && context instanceof Activity) {
                    hasAlreadyAskedForPermission = true;
                    try {
                        waitForUserPermissionRequestToFinish(context);
                        ((Activity) context).requestPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, Config.PermissionRequest.codeID);
                    } catch (Exception e) {
                        userPermissionRequestFinished = true;
                    }
                }
                return false;
            }
        } catch(Exception e){
            Error.log(e);
        }
        return false;
    }

    private static void waitForUserPermissionRequestToFinish(Context context){    //Instead of timer, could use the normal permission callback listener but do not want to add listeners to KatScan as it could interfere negatively with the rest of the application and its permission callback listeners.
        try {
            if (permissionRequestTimer == null && !userPermissionRequestFinished && context != null) {
                permissionRequestTimer = new CountDownTimer(Config.PermissionRequest.millisecondsPerAcceptPermissionsCheck, 1000) {
                    public void onTick(long millisUntilFinished) {
                    }

                    public void onFinish() {
                        try {
                            if (isWriteStoragePermissionGranted(context) || userPermissionRequestFinished) {
                                userPermissionRequestFinished = true;
                                permissionRequestTimer = null;
                            } else {
                                permissionRequestTimeCounter = permissionRequestTimeCounter + 1;
                                if (!userPermissionRequestFinished && permissionRequestTimer != null && !properPermissionsGranted && context != null &&
                                        Config.PermissionRequest.maxWaitTimeForUserToAcceptPermissions > permissionRequestTimeCounter * Config.PermissionRequest.millisecondsPerAcceptPermissionsCheck) {

                                    permissionRequestTimer.start();
                                } else {
                                    userPermissionRequestFinished = true;
                                    Error.log(KATSCAN_ERROR_PREFIX + WRITE_PERM + " permission was not granted or waiting for user input timed-out.\n" +
                                            "\tKat.scan() calls cannot write to files because of it.\n" +
                                            "\tTo grant the required permission needed either restart your application and grant when the permission request appears or find your project in your device's application list under your device's settings and enable it there.");
                                }
                            }
                        } catch(Exception e){
                            userPermissionRequestFinished = true;
                            Error.log(e);
                        }
                    }
                };
                permissionRequestTimer.start();
            }
        } catch(Exception e){
            userPermissionRequestFinished = true;
            Error.log(e);
        }
    }

    private static boolean isWriteStoragePermissionGranted(Context context){
        try {
            if (properPermissionsGranted) {
                return properPermissionsGranted;
            }
            if (context != null && !properPermissionsGranted) {
                properPermissionsGranted = context.checkCallingOrSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
                if (properPermissionsGranted) {
                    userPermissionRequestFinished = true;
                }
                return properPermissionsGranted;
            }
        } catch(Exception e){
            if(!hasWriteStoragePermissionGrantedErrorMessageDisplayed && Config.InternalErrors.showPermissionGrantedErrorIfOccurs) {
                hasWriteStoragePermissionGrantedErrorMessageDisplayed = true;
                Error.log(e);
            }
        }
        return false;
    }

    private static boolean katScanEnabled(){
        return applicationRunningInDebug || enableKatScanRegardlessIfRunningInDebug;
    }

    private static Boolean designatedWriteLocation(Boolean writeKatScanEntriesToFileInsteadOfLogMustBeTrue) {
        if(Config.File.writeKatScanEntriesToFileAndLog) {
            return true;
        }

        if(writeKatScanEntriesToFileInsteadOfLogMustBeTrue) {
            return Config.File.writeKatScanEntriesToFileInsteadOfLog;
        } else {
            return !Config.File.writeKatScanEntriesToFileInsteadOfLog;
        }
    }

    private static void executeConsolePrint(Object message){
        if(applicationRunningInDebug) {
            String txt = String.valueOf(message);
            if (Config.InternalErrors.showKatScanInternallyCaughtErrorsAsLogsOverPrintln) {
                String tag = String.valueOf(Config.InternalErrors.logTag);
                switch (Config.InternalErrors.loggingMethod) {
                    case ERROR:
                        Log.e(tag, txt);
                        break;
                    case WARNING:
                        Log.w(tag, txt);
                        break;
                    case INFORMATION:
                        Log.i(tag, txt);
                        break;
                    case DEBUG:
                        Log.d(tag, txt);
                        break;
                    case VERBOSE:
                        Log.v(tag, txt);
                        break;
                    default:
                        Log.d(tag, txt);
                }
            } else {
                System.err.println(txt);
            }
        }
    }

    private static class Storage {
        private static boolean createDirectory(String directory, boolean directoryContainsFile){
            try{
                if(directoryContainsFile){
                    File directoryAsFile = new File(directory);
                    String dirFileNameStr = directoryAsFile.getName();
                    directory = directory.replace(dirFileNameStr,"");
                }
            } catch(Exception e){
                Error.log(e);
            }
            return createDirectory(directory);
        }

        private static boolean createDirectory(String directory){
            try{
                return createDirectory(new File(directory));
            } catch(Exception e){
                return Error.log(e);
            }
        }

        private static boolean createDirectory(File directory){
            try{
                if(!directory.exists()){
                    directory.mkdirs();
                }
                return directory.exists() && directory.isDirectory();
            } catch (Exception e){
                return Error.log(e);
            }
        }

        private static boolean createFile(String fileWithPath){
            try{
                if(createDirectory(fileWithPath, true)){
                    (new File(fileWithPath)).createNewFile();
                    return true;
                } else {
                    Error.log(KATSCAN_ERROR_PREFIX + " Could not create file as directory could not be created: " + fileWithPath);
                    return false;
                }
            } catch(Exception e){
                return Error.log(e);
            }
        }
    }

    private static class Error{
        private static boolean log(Object message){
            return Error.log(null, message);
        }

        private static boolean log(Exception exception){
            try {
                if (exception != null) {
                    return Error.log(exception, exception.getMessage());
                }
            } catch(Exception e){
                Error.log(e.getMessage());
            }
            return false;
        }

        private static boolean log(Exception exception, Object message){
            try {
                if(applicationRunningInDebug && Config.InternalErrors.showKatScanInternallyCaughtErrors) {
                    String dateNow = "";
                    if(Config.Date.includePrefixedDateForEachLogEntry){
                        dateNow = getEntryDate() + String.valueOf(Config.spaceSeparator);
                    }

                    executeConsolePrint(dateNow + String.valueOf(message));
                    if (exception != null) {
                        executeConsolePrint(dateNow + exceptionToString(exception));
                    }
                }
            } catch(Exception e){
            }
            return false;
        }
    }

    /**
     * All public configurable settings within KatScan that can be updated at run-time by the developer to their liking
     */
    public static class Config{
        public static class File{
            public static String rootDirectoryPath = DEFAULT_ROOT_DIRECTORY_PATH;   //The absolute path just before the "mainDirectoryName" which stores all Kat.scan() entries for this project. | Default: [InternalStorage]/KatScan_[AppPackageName]/
            public static String mainDirectoryName = DEFAULT_MAIN_DIRECTORY_NAME;   //The directory name that is created at the very end of the "rootDirectoryPath" which stores all KatScan entries for this project. | Default: "KatScan_[AppPackageName]/"
            public static String defaultFileName = DEFAULT_FILE_NAME;               //The name of the default file that Kat.scan() entries are written to if the developer does not pass in their own directory path/file to write to instead. | Default: KatScan_log
            public static String fileExtension = DEFAULT_FILE_EXTENSION;            //The file extension for all Kat.scan() files. This includes all instances including default file and user passed in files. | Default: ".txt"
            public static boolean lineBreakBetweenEachEntry = false;                //Add a line break between each Kat.scan() entry to the affected file. | Default: false
            public static boolean writeKatScanEntriesToFileInsteadOfLog = true;     //Each Kat.scan() call will be written to an external file on the user's device instead of writing to the console. | Default: true
            public static boolean writeKatScanEntriesToFileAndLog = true;           //If true, writeKatScanEntriesToFileInsteadOfLog state will not be observed. When true, each Kat.scan() call will be written to an external file on the user's device and  written to the console. | Default: true
            public static boolean writeCountWithEveryEntry = false;                 //For the lifespan of the running application, each Kat.scan() call increments an internal value by 1 starting with 0. Include this value in the written entry output. | Default: false
            public static boolean addEntriesIntoSubdirectoryCreatedToday = true;    //To have each Kat.scan() entry written into a subfolder of the current day within "mainDirectoryName". | Default: true

            public static String getRootDirectoryPath(){
                return dirOrDefault(rootDirectoryPath, DEFAULT_ROOT_DIRECTORY_PATH);
            }

            public static String getMainDirectoryName(){
                return dirOrDefault(mainDirectoryName, DEFAULT_MAIN_DIRECTORY_NAME);
            }

            public static String getFullPathToMainDirectory(){                     //Returns full path needed to write Kat.scan entry() by concatenating the "rootDirectoryPath" value and "mainDirectoryName" value. | Default example:  /storage/emulated/0/KatScan_com.digidemic.katscanexamples/
                return getRootDirectoryPath() + getMainDirectoryName();
            }

            private static String dirOrDefault(String dir, String defaultDir){
                return makeStrDir(dir == null || dir.length() == 0 ? defaultDir : dir);
            }

            private static String makeStrDir(String str){                           //Ensures strings that are defined as directories end with a directory delimiter "/" and if not one is added
                try {
                    if (str != null && str.length() > 0) {
                        str = str.trim();
                        return str + (!str.endsWith(DIR_DELIMITER) ? DIR_DELIMITER : "");
                    }
                } catch(Exception e){
                    Error.log(e);
                }
                return "";
            }
        }
        public static class Date{
            public static boolean includePrefixedDateForEachFileEntry = true;                               //Each Kat.scan() call is prefixed with the current date/time when written to the file. | Default: true
            public static String entryDateFormatPattern = DEFAULT_ENTRY_DATE_FORMAT_PATTERN;                //The date format pattern used when "includePrefixedDateForEachFileEntry" is set to true. | Default: "yy-MM-dd_HH:mm:ss"
            public static String subdirectoryDateFormatPattern = DEFAULT_SUB_DIRECTORY_DATE_FORMAT_PATTERN; //The date format pattern used when "addEntriesIntoSubdirectoryCreatedToday" is set to true. | Default: "yyyy-MM-dd"
            public static boolean includePrefixedDateForEachLogEntry = false;                               //Each KatScan error or Kat.scan() meant to be written to the log instead of a file should have the date/time prefixed to its log entry. | Default: false
        }
        public static class InternalErrors{
            public static boolean showPermissionGrantedErrorIfOccurs = true;                        //If this error has not yet occurred or has not been displayed to the user's console yet. | Default: true
            public static boolean showKatScanInternallyCaughtErrors = true;                         //If this error has not yet occurred or has not been displayed to the user's console yet. | Default: true
            public static boolean showTheSetupErrorAsLogOnceIfNeeded = true;                        //If this error has not yet occurred or has not been displayed to the user's console yet. | Default: true
            public static boolean showKatScanInternallyCaughtErrorsAsLogsOverPrintln = true;        //If this error has not yet occurred or has not been displayed to the user's console yet. | Default: true
            public static boolean showKatScanTextInLogsWhenFailureToWriteInFile = true;             //If this error has not yet occurred or has not been displayed to the user's console yet. | Default: true
            public static LOG_METHOD loggingMethod = LOG_METHOD.DEBUG;                              //When anything needs to be logged, whether it be from a KatScan error or a Kat.scan() entry meant for the log, this is the defining log method. | Default: LOG_METHOD.DEBUG
            public static String logTag = DEFAULT_LOG_TAG_NAME;                                     //The log tag of each log performed in the console. | Default: com.digidemic.katscan_entry
            public enum LOG_METHOD{                                                                 //All possible log methods to define for "loggingMethod" variable
                ERROR,
                WARNING,
                INFORMATION,
                DEBUG,
                VERBOSE
            }
        }
        public static class PermissionRequest{
            public static int codeID = DEFAULT_PERMISSION_REQUEST_CODE;                                                     //The request code used when requesting KatScan needed permissions dialog for devices API 23 and higher. | Default: 65496
            public static long millisecondsPerAcceptPermissionsCheck = DEFAULT_MILLI_PER_ACCEPT_PERMISSIONS_CHECK;          //When showing the request permissions dialog a callback listener was not defined solely to not interfere with the main application. A timer is used in its place to determine if the permission has been accepted during the time to popup is on screen. This variable is how frequently the timer should check if the permission has been accepted. | Default: 1000 (milliseconds)
            public static long maxWaitTimeForUserToAcceptPermissions = DEFAULT_MAX_WAIT_TIME_FOR_USER_TO_ACCEPT_PERMISSIONS;//When showing the request permissions dialog a callback listener was not defined solely to not interfere with the main application. A timer is used in its place to determine if the permission has been accepted during the time to popup is on screen.  This variable is the max duration the timer will check for before ending and assuming the permission has been revoked. | Default: 60000 (milliseconds or 1 minute)
        }

        public static String spaceSeparator = DEFAULT_SPACE_SEPARATOR;      //Spacing put in between entry date and message. | Default: " - "
        public static boolean createNewThreadForEachKatScanCall = false;    //Create a new thread for each Kat.scan() call. | Default: false

        /**
         * If the "enableKatScanRegardlessIfRunningInDebug" variable has manually been set to true
         */
        public static boolean hasKatScanBeenEnabledRegardlessIfRunningInDebug(){
            return enableKatScanRegardlessIfRunningInDebug;
        }

        /**
         * If KatScan detected that the application is running in debug build
         */
        public static boolean isApplicationRunningInDebugMode(){
            return applicationRunningInDebug;
        }

        /**
         * KatScan is enabled either by "applicationRunningInDebug" being true or "enableKatScanRegardlessIfRunningInDebug" being true
         */
        public static boolean isKatScanEnabled(){
            return katScanEnabled();
        }

        /**
         * Allows the application to enable KatScan to write entries regardless if debug or release.
         * Applies only when writing entries to files, not when writing to logs (that remains debug builds only).
         * This is can be useful for applications released and want a way to manually enable KatScan through the application.
         * @param enable enable KatScan for writing to external files regardless if debug or release build.
         */
        public static void enableKatScanRegardlessIfRunningInDebug(boolean enable){
            enableKatScanRegardlessIfRunningInDebug(enable, null);
        }

        /**
         * Allows the application to enable KatScan to write entries regardless if debug or release.
         * Applies only when writing entries to files, not when writing to logs (that remains debug builds only).
         * This is can be useful for applications released and want a way to manually enable KatScan through the application.
         * @param enable enable KatScan for writing to external files regardless if debug or release build.
         * @param activity pass in the application's Activity instance if device needs to grant Storage permissions still.
         */
        public static void enableKatScanRegardlessIfRunningInDebug(boolean enable, Activity activity){
            enableKatScanRegardlessIfRunningInDebug = enable;
            requestStoragePermissionIfNotAlreadyGranted(activity);
        }
    }
}