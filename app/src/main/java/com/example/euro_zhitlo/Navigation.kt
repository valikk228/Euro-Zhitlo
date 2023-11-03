import android.content.Intent
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.startActivity
import com.example.euro_zhitlo.Landlord.LandlordMainActivity
import com.example.euro_zhitlo.Landlord.LandlordMessageActivity
import com.example.euro_zhitlo.Landlord.LandlordProfileActivity
import com.example.euro_zhitlo.R
import com.example.euro_zhitlo.Refugee.RefugeeMainActivity
import com.example.euro_zhitlo.Refugee.RefugeeMessageActivity
import com.example.euro_zhitlo.Refugee.RefugeeProfileActivity
import com.example.euro_zhitlo.Refugee.RefugeeSearchActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class Navigation(private val context: AppCompatActivity) {

    fun showLandlordNavigation(bottomNavigationView:BottomNavigationView,num:Int) {

        bottomNavigationView.menu.getItem(num).isChecked = true
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.myposts -> {
                    if(bottomNavigationView.selectedItemId != R.id.myposts) {
                        val messageIntent = Intent(context, LandlordMainActivity::class.java)
                        startActivity(context, messageIntent, null)
                        context.overridePendingTransition(0, 0)
                        context.finish()
                    }
                    true
                }
                R.id.message -> {
                    if(bottomNavigationView.selectedItemId != R.id.message) {
                        val messageIntent = Intent(context, LandlordMessageActivity::class.java)
                        startActivity(context, messageIntent, null)
                        context.overridePendingTransition(0, 0)
                        context.finish()
                    }
                    true
                }
                R.id.profile -> {
                    if(bottomNavigationView.selectedItemId != R.id.profile) {
                        val profileIntent = Intent(context, LandlordProfileActivity::class.java)
                        startActivity(context, profileIntent, null)
                        context.overridePendingTransition(0, 0)
                        context.finish()
                    }
                    true
                }
                // Додайте обробку інших пунктів меню за потреби
                else -> false
            }
        }
    }

    fun showRefugeeNavigation(bottomNavigationView:BottomNavigationView,num:Int) {

        bottomNavigationView.menu.getItem(num).isChecked = true

        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> {
                    if(bottomNavigationView.selectedItemId != R.id.home) {
                        val profileIntent = Intent(context, RefugeeMainActivity::class.java)
                        startActivity(context, profileIntent, null)
                        context.overridePendingTransition(0, 0)
                        context.finish()
                    }
                    true
                }
                R.id.search -> {
                    if(bottomNavigationView.selectedItemId != R.id.search) {
                        val profileIntent = Intent(context, RefugeeSearchActivity::class.java)
                        startActivity(context, profileIntent, null)
                        context.overridePendingTransition(0, 0)
                        context.finish()
                    }
                    true
                }
                R.id.message -> {
                    if(bottomNavigationView.selectedItemId != R.id.message) {
                        val profileIntent = Intent(context, RefugeeMessageActivity::class.java)
                        startActivity(context, profileIntent, null)
                        context.overridePendingTransition(0, 0)
                        context.finish()
                    }
                    true
                }
                R.id.profile -> {
                    if(bottomNavigationView.selectedItemId != R.id.profile) {
                        val profileIntent = Intent(context, RefugeeProfileActivity::class.java)
                        startActivity(context, profileIntent, null)
                        context.overridePendingTransition(0, 0)
                        context.finish()
                    }
                    true
                }
                // Додайте обробку інших пунктів меню за потреби
                else -> false
            }
        }
    }
}
