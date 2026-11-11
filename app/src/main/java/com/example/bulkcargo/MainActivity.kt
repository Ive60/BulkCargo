package com.example.bulkcargo

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {

    // Each menu item: (title, description, activity class)
    private val menuItems = listOf(
        MenuItemData(
            title = "Stowage Factor Unit Converter",
            desc = "Convert SF between m³/tonne, ft³/long ton, ft³/short ton",
            activity = SfConverter::class.java
        ),
        MenuItemData(
            title = "Weight from Volume & SF",
            desc = "Calculate cargo weight given hold volume and stowage factor",
            activity = CargoWeightFromVolume::class.java
        ),
        MenuItemData(
            title = "Volume from Weight & SF",
            desc = "Calculate required volume given cargo weight and stowage factor",
            activity = CargoVolumeFromWeight::class.java
        ),
        MenuItemData(
            title = "Hold Capacity Utilisation",
            desc = "Check how much cargo fits in each hold, percentage utilisation",
            activity = HoldCapacityUtil::class.java
        ),
        MenuItemData(
            title = "Grains vs Bale Capacity",
            desc = "Convert between grains capacity and bale capacity",
            activity = GrainsVsBale::class.java
        ),
        MenuItemData(
            title = "Multi-Cargo Loading Calc",
            desc = "Calculate total weight/volume for multiple cargo types",
            activity = MultiCargoCalc::class.java
        ),
        MenuItemData(
            title = "Trim & Heeling Estimator",
            desc = "Estimate trim and heeling moments from cargo distribution",
            activity = TrimHeelingEst::class.java
        ),
        MenuItemData(
            title = "Cargo SF Reference Table",
            desc = "Standard stowage factors for common bulk commodities",
            activity = CargoReference::class.java
        )
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val recyclerView: RecyclerView = findViewById(R.id.recyclerViewMenu)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = MenuAdapter(menuItems) { item ->
            startActivity(Intent(this, item.activity))
        }
        recyclerView.addItemDecoration(
            DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        )
    }

    data class MenuItemData(
        val title: String,
        val desc: String,
        val activity: Class<*>
    )

    class MenuAdapter(
        private val items: List<MenuItemData>,
        private val onItemClick: (MenuItemData) -> Unit
    ) : RecyclerView.Adapter<MenuAdapter.MenuViewHolder>() {

        class MenuViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val title: TextView = view.findViewById(R.id.menuTitle)
            val desc: TextView = view.findViewById(R.id.menuDesc)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_menu, parent, false)
            return MenuViewHolder(view)
        }

        override fun onBindViewHolder(holder: MenuViewHolder, position: Int) {
            val item = items[position]
            holder.title.text = item.title
            holder.desc.text = item.desc
            holder.itemView.setOnClickListener { onItemClick(item) }
        }

        override fun getItemCount() = items.size
    }
}
