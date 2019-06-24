package com.mikepenz.fastadapter.app

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.LayoutInflaterCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mikepenz.fastadapter.IItem
import com.mikepenz.fastadapter.adapters.FastItemAdapter
import com.mikepenz.fastadapter.adapters.GenericFastItemAdapter
import com.mikepenz.fastadapter.app.items.expandable.SimpleSubExpandableItem
import com.mikepenz.fastadapter.app.items.expandable.SimpleSubItem
import com.mikepenz.fastadapter.expandable.getExpandableExtension
import com.mikepenz.fastadapter.select.getSelectExtension
import com.mikepenz.iconics.context.IconicsLayoutInflater2
import com.mikepenz.itemanimators.SlideDownAlphaAnimator
import com.mikepenz.materialize.MaterializeBuilder
import kotlinx.android.synthetic.main.activity_sample.*
import java.util.*
import java.util.concurrent.atomic.AtomicLong

class ExpandableSampleActivity : AppCompatActivity() {
    //save our FastAdapter
    private lateinit var fastItemAdapter: GenericFastItemAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        findViewById<View>(android.R.id.content).systemUiVisibility = findViewById<View>(android.R.id.content).systemUiVisibility or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        //as we use an icon from Android-Iconics via xml we add the IconicsLayoutInflater
        //https://github.com/mikepenz/Android-Iconics
        LayoutInflaterCompat.setFactory2(layoutInflater, IconicsLayoutInflater2(delegate))
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sample)

        // Handle Toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.setTitle(R.string.sample_collapsible)

        //style our ui
        MaterializeBuilder().withActivity(this).build()

        //create our FastAdapter
        fastItemAdapter = FastItemAdapter()

        fastItemAdapter.getExpandableExtension()
        val selectExtension = fastItemAdapter.getSelectExtension()
        selectExtension.isSelectable = true
        //expandableExtension.setOnlyOneExpandedItem(true);

        //get our recyclerView and do basic setup
        rv.layoutManager = LinearLayoutManager(this)
        rv.itemAnimator = SlideDownAlphaAnimator()
        rv.adapter = fastItemAdapter

        //fill with some sample data
        val items = ArrayList<GenericItem>()
        val identifier = AtomicLong(1)
        for (i in 1..100) {
            if (i % 3 != 0) {
                val simpleSubItem = SimpleSubItem().withName("Test $i")
                simpleSubItem.identifier = identifier.getAndIncrement()
                items.add(simpleSubItem)
                continue
            }

            val parent = SimpleSubExpandableItem()
            parent.withName("Test $i").identifier = identifier.getAndIncrement()

            val subItems = LinkedList<SimpleSubExpandableItem>()
            for (ii in 1..5) {
                val subItem = SimpleSubExpandableItem()
                subItem.withName("-- SubTest $ii").identifier = identifier.getAndIncrement()

                if (ii % 2 == 0) {
                    continue
                }

                val subSubItems = LinkedList<SimpleSubExpandableItem>()
                for (iii in 1..3) {
                    val subSubItem = SimpleSubExpandableItem()
                    subSubItem.withName("---- SubSubTest $iii").identifier = identifier.getAndIncrement()

                    val subSubSubItems = LinkedList<SimpleSubExpandableItem>()
                    for (iiii in 1..4) {
                        val subSubSubItem = SimpleSubExpandableItem()
                        subSubSubItem.withName("---- SubSubSubTest $iiii").identifier = identifier.getAndIncrement()
                        subSubSubItems.add(subSubSubItem)
                    }
                    subSubItem.subItems.addAll(subSubSubItems)
                    subSubItems.add(subSubItem)
                }
                subItem.subItems.addAll(subSubItems)
                subItems.add(subItem)
            }
            parent.subItems.addAll(subItems)
            items.add(parent)
        }
        fastItemAdapter.add(items)

        //restore selections (this has to be done after the items were added
        fastItemAdapter.withSavedInstanceState(savedInstanceState)

        //set the back arrow in the toolbar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(false)
    }

    override fun onSaveInstanceState(_outState: Bundle) {
        var outState = _outState
        //add the values which need to be saved from the adapter to the bundle
        outState = fastItemAdapter.saveInstanceState(outState)
        super.onSaveInstanceState(outState)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        //handle the click on the back arrow click
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
