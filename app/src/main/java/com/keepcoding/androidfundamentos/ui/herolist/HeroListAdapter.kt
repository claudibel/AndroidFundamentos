package com.keepcoding.androidfundamentos.ui.herolist

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.keepcoding.androidfundamentos.databinding.ItemListBinding
import com.keepcoding.androidfundamentos.model.Hero

class HeroListAdapter(private val clickListener: (Hero) -> (Unit)) :
    RecyclerView.Adapter<HeroListAdapter.HeroLisViewHolder>() {

    var dragonBallHeroes = listOf<Hero>()
    var numberOfHeroesLiving: Int = 17
    var numberOfDeadHeroes : Int = 0
    var numberOfHeroes = dragonBallHeroes.size

    inner class HeroLisViewHolder(private val binding: ItemListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private lateinit var DBHero: Hero

        init {
            binding.selectFighter.setOnClickListener {
                clickListener(DBHero)
            }
        }

        fun bind(dragonBallHero: Hero) {
            println(dragonBallHero.name+" "+dragonBallHero.currentHealth.toString())
            binding.selectFighter.isEnabled = dragonBallHero.currentHealth != 0
            if(dragonBallHero.currentHealth==0){
                numberOfDeadHeroes += 1
                numberOfHeroesLiving = numberOfHeroes - numberOfDeadHeroes
            }

            DBHero = dragonBallHero

            binding.heroImage.load(dragonBallHero.photo)
            binding.heroName.text = dragonBallHero.name
            binding.currentHealth.text = "Current Health: ${dragonBallHero.currentHealth}"
            binding.maxHealth.text = "Maximum Health: ${dragonBallHero.maxHealth}"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HeroLisViewHolder {
        return HeroLisViewHolder(
            ItemListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: HeroLisViewHolder, position: Int) {
        //holder.bind(dragonBallHero[position], position)
        Log.d("Current hero's health", dragonBallHeroes[position].currentHealth.toString())
        holder.bind(dragonBallHeroes[position])
    }

    override fun getItemCount(): Int {
        return dragonBallHeroes.size
    }

    fun updateList(list: List<Hero>) {
        Log.d("Heros received at adapter", list.toString())
        dragonBallHeroes = list
        notifyDataSetChanged()
    }
}