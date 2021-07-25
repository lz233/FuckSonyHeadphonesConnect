package moe.lz233.fuckhpc

import android.app.Application
import android.content.Context
import com.github.kyuubiran.ezxhelper.init.EzXHelperInit
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.callbacks.XC_LoadPackage
import moe.lz233.fuckhpc.utils.LogUtil
import moe.lz233.fuckhpc.utils.ModuleContext
import moe.lz233.fuckhpc.utils.ktx.hookAfterMethod
import org.json.JSONArray
import org.json.JSONObject
import java.net.URL

class InitHook : IXposedHookLoadPackage {
    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        if (lpparam.packageName == PACKAGE_NAME) {
            EzXHelperInit.initHandleLoadPackage(lpparam)
            Application::class.java
                    .hookAfterMethod("attach", Context::class.java) {
                        ModuleContext.context = it.args[0] as Context
                        ModuleContext.classLoader = ModuleContext.context.classLoader
                        EzXHelperInit.setEzClassLoader(ModuleContext.classLoader)
                        initHook()
                    }
        }
    }

    private fun initHook() {
        "jp.co.sony.vim.framework.platform.android.core.util.AndroidCountryUtil"
                .hookAfterMethod("getSelectedIsoCountryCode") {
                    LogUtil.d("jp.co.sony.vim.framework.platform.android.core.util.AndroidCountryUtil.getSelectedIsoCountryCode result ${it.result as String}")
                    it.result = "jp"
                }
        "com.sony.songpal.util.u"
                .hookAfterMethod("b", ByteArray::class.java, Int::class.javaPrimitiveType, Int::class.javaPrimitiveType) {
                    LogUtil.d("com.sony.songpal.util.u.b result ${it.result as String}")
                    it.result = when (it.result as String) {
                        "MDRID294302" -> "MDRID294300"
                        "CN" -> "HK"
                        else -> it.result
                    }
                }
        //call above
        /*"com.sony.songpal.tandemfamily.message.mdr.v1.table1.param.z0"
                .hookAfterMethod("e", ByteArray::class.java) {
                    LogUtil.d("com.sony.songpal.tandemfamily.message.mdr.v1.table1.param.z0.e result ${it.result as String}")
                }*/
        "com.sony.songpal.util.u"
                .hookAfterMethod("a",ByteArray::class.java){
                    LogUtil.d("com.sony.songpal.util.u.a result ${it.result as String}")
                    if ((it.result as String).contains("chargeTime"))
                        it.result = JSONObject()
                                .put("v", "M6")
                                .put("logs", JSONArray()
                                        .put(JSONObject()
                                                .put("key", "opVaNotification")
                                                .put("val", "0"))
                                        .put(JSONObject()
                                                .put("key", "dst")
                                                .put("val", "JP"))
                                        .put(JSONObject()
                                                .put("key", "SPPReject")
                                                .put("val", "5"))
                                        .put(JSONObject()
                                                .put("key", "iAPErr")
                                                .put("val", "0"))
                                        .put(JSONObject()
                                                .put("key", "chargeTime")
                                                .put("val", "106"))).toString()
                }
        "com.sony.songpal.automagic.k"
                .hookAfterMethod("a", String::class.java, String::class.java) {
                    LogUtil.d("com.sony.songpal.automagic.k.a args ${it.args[0] as String} ${it.args[1] as String}")
                    LogUtil.d("com.sony.songpal.automagic.k.a result ${(it.result as URL)}")
                }
        "com.sony.songpal.automagic.i"
                .hookAfterMethod("a", ByteArray::class.java, "com.sony.songpal.automagic.DigestType", "com.sony.songpal.automagic.f") {
                    LogUtil.d("com.sony.songpal.automagic.i.a result ${it.result as String}")
                }
        /*"com.sony.songpal.automagic.i"
            .hookAfterMethod("c")*/
        "com.sony.songpal.automagic.m"
                .hookAfterMethod("c", ByteArray::class.java) {
                    //LogUtil.d("com.sony.songpal.automagic.m.c args ${String(it.args[0] as ByteArray, Charsets.UTF_8)}")
                }
        "com.sony.songpal.ble.client.characteristic.f"
                .hookAfterMethod("d", ByteArray::class.java) {
                    LogUtil.d("com.sony.songpal.ble.client.characteristic.f.d args ${String(it.args[0] as ByteArray)}")
                }
    }
}