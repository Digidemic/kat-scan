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

import android.content.Context;
import android.os.Bundle;

/**
 * Runnable KatScan app.
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
        Kat.setup(context);

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
        Kat.scan("That includes this one.");
        Kat.scan("That includes and this one.");
    }
}
