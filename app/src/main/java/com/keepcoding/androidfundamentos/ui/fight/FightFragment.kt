package com.keepcoding.androidfundamentos.ui.fight

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import coil.load
import com.keepcoding.androidfundamentos.databinding.FragmentFightBinding
import com.keepcoding.androidfundamentos.model.Hero
import com.keepcoding.androidfundamentos.ui.endgame.EndGameFragment
import com.keepcoding.androidfundamentos.ui.endgame.WinnerFragment
import com.keepcoding.androidfundamentos.ui.herolist.HeroListViewModel

class FightFragment(private val combatants: List<Hero>, private var heroList: MutableList<Hero>) :
    Fragment() {

    private var _binding: FragmentFightBinding? = null
    private val binding get() = _binding!!
    private val viewModel: HeroListViewModel by activityViewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentFightBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val hero = combatants[0]
        var herosHealth: Int
        var heroAttack = 0
        var oponentAttack = 0

        val oponent = combatants[1]
        var oponentsHealth: Int


        //Llenando la vista con los datos del heroe elegido por el jugador
        binding.heroImage.load(hero.photo)
        binding.heroName.text = hero.name
        binding.heroMaxHealth.text = "Max Health: " + hero.maxHealth.toString()
        binding.heroCurrentHealth.text = "Current Health: " + hero.currentHealth.toString()

        //Datos del oponente elegido por la app
        binding.oponentImage.load(oponent.photo)
        binding.oponentName.text = oponent.name
        binding.oponentMaxHealth.text = "Max Health: " + oponent.maxHealth.toString()
        binding.oponentCurrentHealth.text = "Current Health: " + oponent.currentHealth.toString()


        //Ajustando el estilo de la Progress Bar del heroe
        //Si la salud es menor de 50, la barra se muestra en rojo

        fun setHerosAtributes(){
            binding.heroProgressBar.max = 100
            binding.heroProgressBar.progress = hero.currentHealth

            binding.heroLowHealthProgressBar.max = 100
            binding.heroLowHealthProgressBar.progress = hero.currentHealth
            binding.heroCurrentHealth.text = "Current health: "+hero.currentHealth.toString()

            if (binding.heroProgressBar.progress < 50) {
                binding.heroProgressBar.visibility = View.GONE
                binding.heroLowHealthProgressBar.visibility = View.VISIBLE
            } else {
                binding.heroProgressBar.visibility = View.VISIBLE
                binding.heroLowHealthProgressBar.visibility = View.GONE
            }
        }

        //Ajustando el estilo de la Progress Bar del oponente
        //Si la salud es menor de 50, la barra se muestra en rojo

        fun setOponentAtributes(){
            binding.oponentProgressBar.max = 100
            binding.oponentProgressBar.progress = oponent.currentHealth

            binding.oponentLowHealthProgressBar.max = 100
            binding.oponentLowHealthProgressBar.progress = oponent.currentHealth
            binding.oponentCurrentHealth.text = "Current health: "+oponent.currentHealth.toString()

            if (binding.oponentProgressBar.progress < 50) {
                binding.oponentProgressBar.visibility = View.GONE
                binding.oponentLowHealthProgressBar.visibility = View.VISIBLE
            } else {
                binding.oponentProgressBar.visibility = View.VISIBLE
                binding.oponentLowHealthProgressBar.visibility = View.GONE
            }
        }

        setHerosAtributes()
        setOponentAtributes()

        val fighters = mutableListOf<Hero>()

        var heroAlive = heroList[0]

        //----------------------BOTON DE PELEA----------------------------------------------------
        binding.fightBtn.setOnClickListener {
            var numberOfDeadHeroes = 0
            var numberOfHeroesAlive = 0
            heroAttack = (10..60).random()
            oponentAttack = (10..60).random()

            herosHealth = hero.currentHealth - oponentAttack
            oponentsHealth = oponent.currentHealth - heroAttack

            if (herosHealth <= 0) {
                herosHealth = 0
            }

            hero.currentHealth = herosHealth

            if (oponentsHealth <= 0) {
                oponentsHealth = 0
            }

            oponent.currentHealth = oponentsHealth

            setHerosAtributes()
            setOponentAtributes()

            for (heroIndex in heroList.indices) {
                if (heroList[heroIndex].id == oponent.id) {
                    heroList.removeAt(heroIndex)
                    heroList.add(heroIndex, oponent)
                }
                if (heroList[heroIndex].id == hero.id) {
                    heroList.removeAt(heroIndex)
                    heroList.add(heroIndex, hero)
                }
            }

            for(heroIndex in heroList.indices){
                if(heroList[heroIndex].currentHealth==0){
                    numberOfDeadHeroes += 1
                }else{
                    heroAlive = heroList[heroIndex]
                }
            }
            numberOfHeroesAlive = heroList.size - numberOfDeadHeroes
            println("Alive: $numberOfHeroesAlive")

            if (herosHealth <= 0 || oponentsHealth <= 0) {
                if(numberOfHeroesAlive>1){
                    childFragmentManager.popBackStackImmediate()
                    viewModel.updateHeroStatsAfterFight(heroList)
                    binding.fightFragment.visibility = View.INVISIBLE
                }
                if(numberOfHeroesAlive==1){
                    val winnerFragment = WinnerFragment(heroAlive)
                    childFragmentManager.beginTransaction()
                        .replace(binding.fightFragment.id, winnerFragment)
                        .commit()
                    binding.fightFragment.visibility = View.INVISIBLE
                    Toast.makeText(requireContext(),"Ganador ${heroAlive.name}", Toast.LENGTH_LONG).show()
                }
                if(numberOfHeroesAlive==0){
                    val endGameFragment = EndGameFragment()
                    parentFragmentManager.beginTransaction()
                        .replace(binding.fightFragment.id, endGameFragment)
                        .commit()
                    binding.fightFragment.visibility = View.INVISIBLE
                    Toast.makeText(requireContext(), "Game Over", Toast.LENGTH_LONG).show()
                }
            }

        }

        //--------------------- BOTON IR HACIA ATRAS---------------------------------------------
        binding.goBackBtn.setOnClickListener {

            childFragmentManager.popBackStackImmediate()

            viewModel.updateHeroStatsAfterFight(heroList)

            binding.fightFragment.visibility = View.INVISIBLE

        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}