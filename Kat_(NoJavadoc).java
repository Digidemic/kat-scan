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

public class Kat {

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

    public static boolean setup(Context context){
        return setup(context, false);
    }

    public static boolean setup(Context context, boolean addEntriesIntoSubdirectoryCreatedToday){
        try{
            if(!setupComplete && context != null) {
                applicationRunningInDebug = ((context.getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0);
                Config.File.addEntriesIntoSubdirectoryCreatedToday = addEntriesIntoSubdirectoryCreatedToday;
                requestStoragePermissionIfNotAlreadyGranted(context);
                Config.File.mainDirectoryName = DEFAULT_KATSCAN_NAME_UNDERSCORE + getApplicationPackageName(context) + DIR_DELIMITER;
                setupComplete = true;
            } else if(setupComplete && context != null && !properPermissionsGranted){
                requestStoragePermissionIfNotAlreadyGranted(context);
            }
        } catch(Exception e){
            Error.log(e);
        }
        return setupComplete;
    }

    public static void scan(Exception exception){ start(exception == null ? "null" : null, exception, null); }

    public static void scan(Object message){
        start(String.valueOf(message), null, null);
    }

    public static void scan(Exception exception, Object message){ start(String.valueOf(message), exception, null); }

    public static void scan(Object addEntryToThisFileName, Exception exception){
        start(exception == null ? "null" : null, exception, String.valueOf(addEntryToThisFileName));
    }

    public static void scan(Object addEntryToThisFileName, Object message){
        start(String.valueOf(message), null, String.valueOf(addEntryToThisFileName));
    }

    public static void scan(Object addEntryToThisFileName, Exception exception, Object message){
        start(String.valueOf(message), exception, String.valueOf(addEntryToThisFileName));
    }

    private static void start(final Object MESSAGE, final Exception EXCEPTION, final Object ADD_ENTRY_TO_THIS_FILE_NAME){
        try {
            if (Config.createNewThreadForEachKatScanCall) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        writeEntry(MESSAGE, EXCEPTION, ADD_ENTRY_TO_THIS_FILE_NAME);
                    }
                }).start();
            } else {
                writeEntry(MESSAGE, EXCEPTION, ADD_ENTRY_TO_THIS_FILE_NAME);
            }
        } catch(Exception e){
            if(katScanEnabled()) {
                Error.log(e);
            }
        }
    }

    private static void writeEntry(Object message, Exception exception, Object addEntryToThisFileName){
        try {
            if (!setupComplete && !hasSetupIncompleteMessageDisplayed && Config.InternalErrors.showTheSetupErrorAsLogOnceIfNeededRegardlessIfDebugging) {
                hasSetupIncompleteMessageDisplayed = true;
                Error.log(KATSCAN_ERROR_PREFIX + "Kat.setup(context); has not been called properly and needs to only once for the lifespan of the application in order to work properly.\n" +
                        "\tInitial setup variables have not been reassigned meaning everything will be saved in their KatScan default paths.\n" +
                        "\tBecause of this, KatScan cannot determine if the application is running in debug or release mode. As a result, all Kat.scan() calls will not write to any files or logs until Kat.setup() is properly called (unless the debug override has been set true, Kat.Config.enableKatScanRegardlessIfRunningInDebug(true);\n" +
                        "\tPlease call Kat.setup(this); in the OnCreate() of your main activity or just once in your project before performing your first Kat.scan() call.\n" +
                        "\tKat.setup(context) can be called from other class instances like Services or Broadcast Receiver but will be unable to request " + WRITE_PERM + " permission if the device is API 23 or higher.\n");
            }
            if (katScanEnabled()) {
                if(Config.File.writeCountWithEveryEntry) {
                    entryCount = entryCount + 1;
                }

                String date = getEntryDate();
                String filePath = constructFilePath(addEntryToThisFileName);

                writeEntryToFile(constructEntryText(message, exception), filePath, date);

                if(Config.File.lineBreakBetweenEachEntry){
                    writeEntryToFile("", filePath, null);
                }
            }
        } catch(Exception e){
            if(katScanEnabled()) {
                Error.log(e);
            }
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

    private static void writeEntryToFile(Object txt, String filePath, String date){
        try {
            if(txt != null && filePath != null && properPermissionsGranted && Config.File.writeKatScanEntriesToFileInsteadOfLog) {
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
                if (Storage.createFile(filePath)) {
                    FileWriter fw = new FileWriter(filePath, true);
                    BufferedWriter bw = new BufferedWriter(fw);
                    PrintWriter out = new PrintWriter(bw);
                    out.println(entry.toString());
                    out.flush();

                    fw.close();
                    bw.close();
                    out.close();
                    out = null;
                    bw = null;
                    fw = null;
                    return;
                } else {
                    Error.log(KATSCAN_ERROR_PREFIX + "File could not be created or does not exist | filePath: " + filePath + " message: " + entry.toString());
                }
            }
        } catch (Exception e) {
            Error.log(e);
        }
        try {
            if (!properPermissionsGranted && !hasDeniedPermissionsMessageDisplayed && Config.File.writeKatScanEntriesToFileInsteadOfLog) {
                hasDeniedPermissionsMessageDisplayed = true;
                Error.log(KATSCAN_ERROR_PREFIX + "Required \"Storage\" permission has not been granted by the device.\n" +
                        "\tKatScan only requires 1 permission,\n" +
                        "\t" + FULL_WRITE_PERM + "\n" +
                        "\twhich needs to be added to the application's AndroidManifest.xml file.\n" +
                        "\tThis permission is needed to output Kat.scan() entries to a separate file.\n" +
                        "\tIf this permission has already been added to the project manifest, make sure Kat.setup(ACTIVITY_HERE) is called just once in your project before your first Kat.scan() call.\n" +
                        "\tIf on a device using API 23 or higher, this will trigger a popup asking the user to grant the \"Storage\" permission your app.\n" +
                        "\tIf the \"Never ask again\" checkbox was checked and the permission was revoked you can also enable this permission by going to the app settings on your device and enabling it there.\n");
            }
            if (txt != null && filePath == null && !hasInvalidPathMessageDisplayed && Config.File.writeKatScanEntriesToFileInsteadOfLog) {
                hasInvalidPathMessageDisplayed = true;
                Error.log(KATSCAN_ERROR_PREFIX + "File path is null so a file could not be written");
            }
            if (txt != null && (Config.InternalErrors.showKatScanTextInLogsWhenFailureToWriteInFile || !Config.File.writeKatScanEntriesToFileInsteadOfLog)) {
                if (!userPermissionRequestFinished && Config.File.writeKatScanEntriesToFileInsteadOfLog) {
                    Error.log(KATSCAN_ERROR_PREFIX + "Currently requesting user to grant permission to use the " + WRITE_PERM + ".\n" +
                            "\tBecause the following Kat.scan entry has been called during requesting time it will only be displaying in the log.");
                }
                Error.log(txt);
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

                sw.close();
                sw = null;
                exception = null;
                sw = null;
                if(exceptionStr != null && exceptionStr.endsWith("\n")){
                    return exceptionStr.substring(0, exceptionStr.length() - 1);
                }
                return exceptionStr;
            }
        } catch(Exception e){
            Error.log(e.getMessage());
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
            } else if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M && katScanEnabled() && !hasAlreadyAskedForPermission && Config.File.writeKatScanEntriesToFileInsteadOfLog) {
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

    private static void waitForUserPermissionRequestToFinish(final Context CONTEXT){
        try {
            if (permissionRequestTimer == null && !userPermissionRequestFinished && CONTEXT != null) {
                permissionRequestTimer = new CountDownTimer(Config.PermissionRequest.millisecondsPerAcceptPermissionsCheck, 1000) {
                    public void onTick(long millisUntilFinished) {
                    }

                    public void onFinish() {
                        try {
                            if (isWriteStoragePermissionGranted(CONTEXT) || userPermissionRequestFinished) {
                                userPermissionRequestFinished = true;
                                permissionRequestTimer = null;
                            } else {
                                permissionRequestTimeCounter = permissionRequestTimeCounter + 1;
                                if (!userPermissionRequestFinished && permissionRequestTimer != null && !properPermissionsGranted && CONTEXT != null &&
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
                String dateNow = "";
                if(Config.Date.includePrefixedDateForEachLogEntry){
                    dateNow = getEntryDate() + String.valueOf(Config.spaceSeparator);
                }
                if(Config.InternalErrors.showKatScanInternallyCaughtErrors) {
                    executeConsolePrint(dateNow + String.valueOf(message));
                    if (exception != null) {
                        executeConsolePrint(dateNow + exceptionToString(exception));
                    }
                }
            } catch(Exception e){
            }
            return false;
        }

        private static void executeConsolePrint(Object message){
            String txt = String.valueOf(message);
            if(Config.InternalErrors.showKatScanInternallyCaughtErrorsAsLogsOverPrintln) {
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

    public static class Config{
        public static class File{
            public static String rootDirectoryPath = DEFAULT_ROOT_DIRECTORY_PATH;
            public static String mainDirectoryName = DEFAULT_MAIN_DIRECTORY_NAME;
            public static String defaultFileName = DEFAULT_FILE_NAME;
            public static String fileExtension = DEFAULT_FILE_EXTENSION;
            public static boolean lineBreakBetweenEachEntry = false;
            public static boolean writeKatScanEntriesToFileInsteadOfLog = true;
            public static boolean writeCountWithEveryEntry = false;
            public static boolean addEntriesIntoSubdirectoryCreatedToday = false;

            public static String getRootDirectoryPath(){
                return dirOrDefault(rootDirectoryPath, DEFAULT_ROOT_DIRECTORY_PATH);
            }

            public static String getMainDirectoryName(){
                return dirOrDefault(mainDirectoryName, DEFAULT_MAIN_DIRECTORY_NAME);
            }

            public static String getFullPathToMainDirectory(){
                return getRootDirectoryPath() + getMainDirectoryName();
            }

            private static String dirOrDefault(String dir, String defaultDir){
                return makeStrDir(dir == null || dir.length() == 0 ? defaultDir : dir);
            }

            private static String makeStrDir(String str){
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
            public static boolean includePrefixedDateForEachFileEntry = true;
            public static String entryDateFormatPattern = DEFAULT_ENTRY_DATE_FORMAT_PATTERN;
            public static String subdirectoryDateFormatPattern = DEFAULT_SUB_DIRECTORY_DATE_FORMAT_PATTERN;
            public static boolean includePrefixedDateForEachLogEntry = false;
        }
        public static class InternalErrors{
            public static boolean showPermissionGrantedErrorIfOccurs = true;
            public static boolean showKatScanInternallyCaughtErrors = true;
            public static boolean showTheSetupErrorAsLogOnceIfNeededRegardlessIfDebugging = true;
            public static boolean showKatScanInternallyCaughtErrorsAsLogsOverPrintln = true;
            public static boolean showKatScanTextInLogsWhenFailureToWriteInFile = true;
            public static LOG_METHOD loggingMethod = LOG_METHOD.DEBUG;
            public static String logTag = DEFAULT_LOG_TAG_NAME;
            public enum LOG_METHOD{
                ERROR,
                WARNING,
                INFORMATION,
                DEBUG,
                VERBOSE
            }
        }
        public static class PermissionRequest{
            public static int codeID = DEFAULT_PERMISSION_REQUEST_CODE;
            public static long millisecondsPerAcceptPermissionsCheck = DEFAULT_MILLI_PER_ACCEPT_PERMISSIONS_CHECK;
            public static long maxWaitTimeForUserToAcceptPermissions = DEFAULT_MAX_WAIT_TIME_FOR_USER_TO_ACCEPT_PERMISSIONS;
        }

        public static String spaceSeparator = DEFAULT_SPACE_SEPARATOR;
        public static boolean createNewThreadForEachKatScanCall = false;

        public static boolean hasKatScanBeenEnabledRegardlessIfRunningInDebug(){
            return enableKatScanRegardlessIfRunningInDebug;
        }

        public static boolean isApplicationRunningInDebugMode(){
            return applicationRunningInDebug;
        }

        public static boolean isKatScanEnabled(){
            return katScanEnabled();
        }

        public static void enableKatScanRegardlessIfRunningInDebug(boolean enable){
            enableKatScanRegardlessIfRunningInDebug(enable, null);
        }

        public static void enableKatScanRegardlessIfRunningInDebug(boolean enable, Activity activity){
            enableKatScanRegardlessIfRunningInDebug = enable;
            requestStoragePermissionIfNotAlreadyGranted(activity);
        }
    }
}