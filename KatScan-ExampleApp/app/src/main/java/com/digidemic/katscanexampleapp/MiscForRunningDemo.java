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

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Paint;
import android.net.Uri;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

/**
 * NO DOCUMENTATION FOR KatScan IN THIS CLASS!! ALL CODE HERE IS FOR RUNNING THE EXAMPLE APPLICATION
 * NO DOCUMENTATION FOR KatScan IN THIS CLASS!! ALL CODE HERE IS FOR RUNNING THE EXAMPLE APPLICATION
 * NO DOCUMENTATION FOR KatScan IN THIS CLASS!! ALL CODE HERE IS FOR RUNNING THE EXAMPLE APPLICATION
 * NO DOCUMENTATION FOR KatScan IN THIS CLASS!! ALL CODE HERE IS FOR RUNNING THE EXAMPLE APPLICATION
 * NO DOCUMENTATION FOR KatScan IN THIS CLASS!! ALL CODE HERE IS FOR RUNNING THE EXAMPLE APPLICATION
 * NO DOCUMENTATION FOR KatScan IN THIS CLASS!! ALL CODE HERE IS FOR RUNNING THE EXAMPLE APPLICATION
 * NO DOCUMENTATION FOR KatScan IN THIS CLASS!! ALL CODE HERE IS FOR RUNNING THE EXAMPLE APPLICATION
 * NO DOCUMENTATION FOR KatScan IN THIS CLASS!! ALL CODE HERE IS FOR RUNNING THE EXAMPLE APPLICATION
 * NO DOCUMENTATION FOR KatScan IN THIS CLASS!! ALL CODE HERE IS FOR RUNNING THE EXAMPLE APPLICATION
 * NO DOCUMENTATION FOR KatScan IN THIS CLASS!! ALL CODE HERE IS FOR RUNNING THE EXAMPLE APPLICATION
 * NO DOCUMENTATION FOR KatScan IN THIS CLASS!! ALL CODE HERE IS FOR RUNNING THE EXAMPLE APPLICATION
 * NO DOCUMENTATION FOR KatScan IN THIS CLASS!! ALL CODE HERE IS FOR RUNNING THE EXAMPLE APPLICATION
 * NO DOCUMENTATION FOR KatScan IN THIS CLASS!! ALL CODE HERE IS FOR RUNNING THE EXAMPLE APPLICATION
 * NO DOCUMENTATION FOR KatScan IN THIS CLASS!! ALL CODE HERE IS FOR RUNNING THE EXAMPLE APPLICATION
 * NO DOCUMENTATION FOR KatScan IN THIS CLASS!! ALL CODE HERE IS FOR RUNNING THE EXAMPLE APPLICATION
 * NO DOCUMENTATION FOR KatScan IN THIS CLASS!! ALL CODE HERE IS FOR RUNNING THE EXAMPLE APPLICATION
 * NO DOCUMENTATION FOR KatScan IN THIS CLASS!! ALL CODE HERE IS FOR RUNNING THE EXAMPLE APPLICATION
 * NO DOCUMENTATION FOR KatScan IN THIS CLASS!! ALL CODE HERE IS FOR RUNNING THE EXAMPLE APPLICATION
 * NO DOCUMENTATION FOR KatScan IN THIS CLASS!! ALL CODE HERE IS FOR RUNNING THE EXAMPLE APPLICATION
 * NO DOCUMENTATION FOR KatScan IN THIS CLASS!! ALL CODE HERE IS FOR RUNNING THE EXAMPLE APPLICATION
 * NO DOCUMENTATION FOR KatScan IN THIS CLASS!! ALL CODE HERE IS FOR RUNNING THE EXAMPLE APPLICATION
 * NO DOCUMENTATION FOR KatScan IN THIS CLASS!! ALL CODE HERE IS FOR RUNNING THE EXAMPLE APPLICATION
 * NO DOCUMENTATION FOR KatScan IN THIS CLASS!! ALL CODE HERE IS FOR RUNNING THE EXAMPLE APPLICATION
 * NO DOCUMENTATION FOR KatScan IN THIS CLASS!! ALL CODE HERE IS FOR RUNNING THE EXAMPLE APPLICATION
 * NO DOCUMENTATION FOR KatScan IN THIS CLASS!! ALL CODE HERE IS FOR RUNNING THE EXAMPLE APPLICATION
 * NO DOCUMENTATION FOR KatScan IN THIS CLASS!! ALL CODE HERE IS FOR RUNNING THE EXAMPLE APPLICATION
 * NO DOCUMENTATION FOR KatScan IN THIS CLASS!! ALL CODE HERE IS FOR RUNNING THE EXAMPLE APPLICATION
 * NO DOCUMENTATION FOR KatScan IN THIS CLASS!! ALL CODE HERE IS FOR RUNNING THE EXAMPLE APPLICATION
 * NO DOCUMENTATION FOR KatScan IN THIS CLASS!! ALL CODE HERE IS FOR RUNNING THE EXAMPLE APPLICATION
 * NO DOCUMENTATION FOR KatScan IN THIS CLASS!! ALL CODE HERE IS FOR RUNNING THE EXAMPLE APPLICATION
 * NO DOCUMENTATION FOR KatScan IN THIS CLASS!! ALL CODE HERE IS FOR RUNNING THE EXAMPLE APPLICATION
 * NO DOCUMENTATION FOR KatScan IN THIS CLASS!! ALL CODE HERE IS FOR RUNNING THE EXAMPLE APPLICATION
 * NO DOCUMENTATION FOR KatScan IN THIS CLASS!! ALL CODE HERE IS FOR RUNNING THE EXAMPLE APPLICATION
 * NO DOCUMENTATION FOR KatScan IN THIS CLASS!! ALL CODE HERE IS FOR RUNNING THE EXAMPLE APPLICATION
 * NO DOCUMENTATION FOR KatScan IN THIS CLASS!! ALL CODE HERE IS FOR RUNNING THE EXAMPLE APPLICATION
 * NO DOCUMENTATION FOR KatScan IN THIS CLASS!! ALL CODE HERE IS FOR RUNNING THE EXAMPLE APPLICATION
 * NO DOCUMENTATION FOR KatScan IN THIS CLASS!! ALL CODE HERE IS FOR RUNNING THE EXAMPLE APPLICATION
 * NO DOCUMENTATION FOR KatScan IN THIS CLASS!! ALL CODE HERE IS FOR RUNNING THE EXAMPLE APPLICATION
 * NO DOCUMENTATION FOR KatScan IN THIS CLASS!! ALL CODE HERE IS FOR RUNNING THE EXAMPLE APPLICATION
 * NO DOCUMENTATION FOR KatScan IN THIS CLASS!! ALL CODE HERE IS FOR RUNNING THE EXAMPLE APPLICATION
 * NO DOCUMENTATION FOR KatScan IN THIS CLASS!! ALL CODE HERE IS FOR RUNNING THE EXAMPLE APPLICATION
 * NO DOCUMENTATION FOR KatScan IN THIS CLASS!! ALL CODE HERE IS FOR RUNNING THE EXAMPLE APPLICATION
 * NO DOCUMENTATION FOR KatScan IN THIS CLASS!! ALL CODE HERE IS FOR RUNNING THE EXAMPLE APPLICATION
 * NO DOCUMENTATION FOR KatScan IN THIS CLASS!! ALL CODE HERE IS FOR RUNNING THE EXAMPLE APPLICATION
 * NO DOCUMENTATION FOR KatScan IN THIS CLASS!! ALL CODE HERE IS FOR RUNNING THE EXAMPLE APPLICATION
 * NO DOCUMENTATION FOR KatScan IN THIS CLASS!! ALL CODE HERE IS FOR RUNNING THE EXAMPLE APPLICATION
 * NO DOCUMENTATION FOR KatScan IN THIS CLASS!! ALL CODE HERE IS FOR RUNNING THE EXAMPLE APPLICATION
 * NO DOCUMENTATION FOR KatScan IN THIS CLASS!! ALL CODE HERE IS FOR RUNNING THE EXAMPLE APPLICATION
 * NO DOCUMENTATION FOR KatScan IN THIS CLASS!! ALL CODE HERE IS FOR RUNNING THE EXAMPLE APPLICATION
 */
public class MiscForRunningDemo extends Activity{
    private TextView codeRan;
    private TextView topText;
    private Button btnPathOpen;
    private Button btnRunCodeAgain;

    private static int codeRanCount = 0;
    private static boolean permissionGranted = true;
    private static boolean initCalled = false;
    private static boolean doNotNeedToRequestPermission = false;

    private static final String EXAMPLE_CODE_IN_TEXT = "" +
            "//Only pre-req call needed and only called once for the entirety of the application life!\n" +
            "Kat.setup(this, true);//Context and if should write to subdirectory of today's date\n" +
            "\n" +
            "//Pass in variables, exceptions, custom messages, or any form of text for KatScan to output to a separate file.\n" +
            "Kat.scan(\"Setup complete, This custom message written and time stamped to default file/directory:\" +\n" +
            "   \"/storage/emulated/0/KatScan_YOUR.PACKAGE.HERE/yyyy-MM-dd/KatScan_log.txt\");\n" +
            "try{\n" +
            "   //Explicitly throw an error for this example\n" +
            "   String arrayOutOfBounds = new String[]{\"KatScan\", \"error\", \"example\"}[3];\n" +
            "\n" +
            "} catch(Exception exception){\n" +
            "\n" +
            "   //Pass in exception to have its full stack trace written to its default file.\n" +
            "   Kat.scan(exception);\n" +
            "\n" +
            "   //Or have the exception stack trace be prefixed with a custom message.\n" +
            "   Kat.scan(exception, \"Exception stack trace with this custom message written to default file.\");\n" +
            "\n" +
            "   //Or even have everything written to a new directory & file KatScan creates if does not already exist\n" +
            "   Kat.scan(\"/Errors/log.txt\", exception, \"Exception stack trace with this custom message written to new subdirectory and file.\");\n" +
            "}\n" +
            "\n" +
            "//Dozens of configurable settings that can be updated at run-time\n" +
            "Kat.Config.File.lineBreakBetweenEachEntry = true;\n" +
            "Kat.scan(\"Every Kat.scan() going forward will have a line break between entries.\");\n" +
            "Kat.Config.createNewThreadForEachKatScanCall = true;\n" +
            "Kat.scan(\"This custom message was written using a new thread.\");";

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if(requestCode == Kat.Config.PermissionRequest.codeID) {
            if(grantResults != null && grantResults.length > 0){
                permissionGranted = grantResults[0] == 0;
                doNotNeedToRequestPermission = true;
                runExample(null);
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    protected void init(){
        if(!initCalled) {
            initCalled = true;

            codeRan = (TextView) findViewById(R.id.codeRan);
            topText = (TextView) findViewById(R.id.topText);
            btnPathOpen = (Button) findViewById(R.id.btnPathOpen);
            btnRunCodeAgain = (Button) findViewById(R.id.btnRunCodeAgain);
            try {
                topText.setPaintFlags(topText.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
            } catch (Exception e) {
            }
            codeRan.setText(EXAMPLE_CODE_IN_TEXT);

            doNotNeedToRequestPermission = checkCallingOrSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        }
    }

    public void openPath(View v){
        String mainDir = Kat.Config.File.getFullPathToMainDirectory();
        Uri selectedUri = Uri.parse(mainDir);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(selectedUri, "*/*");
        if (intent.resolveActivityInfo(getPackageManager(), 0) != null) {
            startActivity(intent);
        } else {
            toast("Could not open directory: " + mainDir);
        }
    }

    public void runExample(View v){
        if(doNotNeedToRequestPermission) {
            if (permissionGranted) {
                MainActivity.runExample(this);
                codeRanCount += 1;
                btnPathOpen.setVisibility(View.VISIBLE);
                btnPathOpen.setText("Open Results Directory using a File Viewer\n\n" + Kat.Config.File.getFullPathToMainDirectory());
                btnRunCodeAgain.setText("Run Example Code Again");
                topText.setText("The following code has ran " + codeRanCount + " time(s)");
                toast("Example code ran successfully!");
            } else {
                toast("Storage permission revoked.\nRestart the app and grant permission or manually do so in device settings.");
                topText.setText("Permission revoked. KatScan could not write to file so instead wrote to log");
            }
        } else {
            MainActivity.runExample(this);
        }
    }

    private void toast(String msg){
        Toast.makeText(this, String.valueOf(msg), Toast.LENGTH_LONG).show();
    }
}
