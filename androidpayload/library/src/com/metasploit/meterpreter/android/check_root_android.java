package com.metasploit.meterpreter.android;

import java.io.File;

import com.metasploit.meterpreter.Meterpreter;
import com.metasploit.meterpreter.TLVPacket;
import com.metasploit.meterpreter.command.Command;

public class check_root_android implements Command {

    private static final int TLV_EXTENSIONS = 20000;
    private static final int TLV_TYPE_CHECK_ROOT_BOOL =
		TLVPacket.TLV_META_TYPE_BOOL | (TLV_EXTENSIONS + 9019);

    @Override
    public int execute(Meterpreter meterpreter, TLVPacket request,
                       TLVPacket response) throws Exception {

        response.addOverflow(TLV_TYPE_CHECK_ROOT_BOOL, isRooted());

        return ERROR_SUCCESS;
    }

    public static boolean isRooted() {
        String buildTags = android.os.Build.TAGS;
        if (buildTags != null && buildTags.contains("test-keys")) {
            return true;
        }

        return fileInstalled("/system/app/SuperSU")
                || fileInstalled("/system/app/SuperSU.apk")
                || fileInstalled("/system/app/Superuser")
                || fileInstalled("/system/app/Superuser.apk")
                || fileInstalled("/system/xbin/su")
                || fileInstalled("/system/xbin/_su")
                || canExecuteCommand("/system/xbin/which su")
                || canExecuteCommand("/system/bin/which su")
                || canExecuteCommand("which su");
    }

    private static boolean fileInstalled(String packageName) {
        boolean installed;
        try {
            File file = new File("/system/app/" + packageName);
            installed = file.exists();
        } catch (Exception e1){
            installed = false;
        }

        return installed;
    }

    private static boolean canExecuteCommand(String command) {
        boolean executedSuccesfully;
        try {
            Runtime.getRuntime().exec(command);
            executedSuccesfully = true;
        } catch (Exception e) {
            executedSuccesfully = false;
        }

        return executedSuccesfully;
    }

}
