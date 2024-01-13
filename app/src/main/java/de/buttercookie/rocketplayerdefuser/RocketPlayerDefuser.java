/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package de.buttercookie.rocketplayerdefuser;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import static de.robv.android.xposed.XposedHelpers.findClass;

import android.os.Bundle;

import androidx.annotation.Keep;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class RocketPlayerDefuser implements IXposedHookLoadPackage {

    private static final String LOGTAG = "RocketPlayerDefuser";

    private static final int EXPIRED_VERSION_DIALOG = 13;

    @Keep
    public RocketPlayerDefuser() {}

    @Keep
    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        if (!lpparam.packageName.equals("com.jrtstudio.AnotherMusicPlayer")) {
            return;
        }

        final Class<?> targetClass =
                findClass("com.jrtstudio.AnotherMusicPlayer.ActivityMusicBrowser", lpparam.classLoader);

        findAndHookMethod("android.app.Activity", lpparam.classLoader,
                "showDialog", int.class, Bundle.class, new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        if (!(param.thisObject.getClass() == targetClass)) {
                            return;
                        }

                        if ((int) param.args[0] == EXPIRED_VERSION_DIALOG) {
                            param.setResult(false);
                        }
                    }
                });
    }
}
