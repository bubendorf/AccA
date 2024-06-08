package mattecarra.accapp

import android.annotation.SuppressLint
import android.content.Context
import android.widget.Toast
import androidx.multidex.MultiDexApplication
import androidx.preference.PreferenceManager.getDefaultSharedPreferences
import com.topjohnwu.superuser.Shell
import mattecarra.accapp.utils.LogExt
import org.acra.ACRA.init
import org.acra.config.CoreConfigurationBuilder
import org.acra.config.HttpSenderConfigurationBuilder
import org.acra.config.LimiterConfigurationBuilder
import org.acra.config.ToastConfigurationBuilder
import org.acra.data.StringFormat
import org.acra.security.TLS
import org.acra.sender.HttpSender

class MainApplication: MultiDexApplication()
{
    companion object
    {
        var mDEBUG: Int = 0

        init
        {
            Shell.Config.setFlags(Shell.FLAG_REDIRECT_STDERR)
            Shell.Config.verboseLogging(BuildConfig.DEBUG)
            Shell.Config.setTimeout(10)
        }
    }

    @SuppressLint("LogNotTimber")
    override fun onCreate()
    {
        super.onCreate()
        mDEBUG = (getDefaultSharedPreferences(applicationContext).getString("appdebug", "0") ?: "0").toInt()
        LogExt().s(javaClass.simpleName, "DEBUG=$mDEBUG " +when(mDEBUG) {0->"[NONE]" 1->"[CONSOLE]" 2->"[FILE]" else->"[UNKNOWN]"})
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
//        if (!BuildConfig.DEBUG) {
            init(
                this, CoreConfigurationBuilder() //core configuration:
                    .withBuildConfigClass(BuildConfig::class.java)
                    .withReportFormat(StringFormat.JSON)
                    .withPluginConfigurations( //each plugin you chose above can be configured with its builder like this:
                        ToastConfigurationBuilder()
                            .withText("AccA crashed. Ein Bericht wurde an Markus verschickt.")
                            .withLength(Toast.LENGTH_LONG)
                            .build(),
                        HttpSenderConfigurationBuilder() //required. Https recommended
                            .withUri("https://acra.bubendorf.net/report") //optional. Enables http basic auth
                            .withBasicAuthLogin("KHTsEzvtHFxh8czn") //required if above set
                            .withBasicAuthPassword("Kh7EOjGiMihWzHBE") // defaults to POST
                            .withHttpMethod(HttpSender.Method.POST) //defaults to 5000ms
                            .withConnectionTimeout(5000) //defaults to 20000ms
                            .withSocketTimeout(20000) // defaults to false
                            .withDropReportsOnTimeout(false) //defaults to false. Recommended if your backend supports it
                            .withCompress(true) //defaults to all
                            .withTlsProtocols(TLS.V1_3, TLS.V1_2)
                            .build(),
                        LimiterConfigurationBuilder()
                            .withDeleteReportsOnAppUpdate(true)
                            .withEnabled(true)
                            .build()
                    )
            )
//        }
    }
}
