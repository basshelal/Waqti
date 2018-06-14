package uk.whitecrescent.waqti.android

import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.design.widget.Snackbar
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.nav_header_main.*
import uk.whitecrescent.waqti.R
import uk.whitecrescent.waqti.model.shortSnackbar

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        fab.setOnClickListener {
            Snackbar.make(it, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }

        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)
        drawer_layout.addDrawerListener(object : DrawerLayout.DrawerListener {
            override fun onDrawerStateChanged(newState: Int) {}

            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {}

            override fun onDrawerClosed(drawerView: View) {}

            override fun onDrawerOpened(drawerView: View) {
                if (!imageView.hasOnClickListeners()) {
                    imageView.setOnClickListener {
                        shortSnackbar(drawerView, "Android!")
                        drawer_layout.closeDrawer(GravityCompat.START)
                    }
                }
            }

        })
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when (item.itemId) {
            R.id.action_settings -> return true
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_camera -> {
                shortSnackbar(nav_view, "Camera")
                drawer_layout.closeDrawer(GravityCompat.START)
            }
            R.id.nav_gallery -> {
                shortSnackbar(nav_view, "Gallery")
                drawer_layout.closeDrawer(GravityCompat.START)
            }
            R.id.nav_slideshow -> {
                shortSnackbar(nav_view, "Slideshow")
                drawer_layout.closeDrawer(GravityCompat.START)
            }
            R.id.nav_manage -> {
                shortSnackbar(nav_view, "Manage")
                drawer_layout.closeDrawer(GravityCompat.START)
            }
            R.id.nav_share -> {
                shortSnackbar(nav_view, "Share")
            }
            R.id.nav_send -> {
                shortSnackbar(nav_view, "Send")
            }
        }

        return true
    }
}
