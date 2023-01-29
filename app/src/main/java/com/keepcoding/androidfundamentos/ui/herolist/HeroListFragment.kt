package com.keepcoding.androidfundamentos.ui.herolist

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.keepcoding.androidfundamentos.databinding.FragmentHeroListBinding
import com.keepcoding.androidfundamentos.model.Hero
import com.keepcoding.androidfundamentos.ui.fight.FightFragment

class HeroListFragment: Fragment() {

    private var _binding: FragmentHeroListBinding? = null
    private val binding get() = _binding!!

    //Aqui se envian los datos al FightFragment tanto del peleador elegido por el usuario como del
    //elegido por la aplicacion. En una lista se envian los dos peleadores(combatants) y en la otra la lista
    //completa de heroes(filteredList). Se verifica que los peleadores no sean el mismo personaje
    private val adapter = HeroListAdapter{
        val combatants = mutableListOf<Hero>()
        combatants.add(it)
        val oponentNumber = (0 until filteredList.size).random()
        var oponent: Hero
        if(it.name != filteredList[oponentNumber].name && filteredList[oponentNumber].currentHealth !=0){
            oponent = filteredList[oponentNumber]
            combatants.add(oponent)
        }else{
            if(oponentNumber+1==filteredList.size){
                for(opNumber in oponentNumber-1 downTo 0){
                    if(it.name != filteredList[opNumber].name && filteredList[opNumber].currentHealth !=0){
                        oponent = filteredList[opNumber]
                        combatants.add(oponent)
                        break
                    }
                }

            } else{
                for(opNumber in filteredList.indices){
                    if(it.name != filteredList[opNumber].name && filteredList[opNumber].currentHealth !=0){
                        oponent = filteredList[opNumber]
                        combatants.add(oponent)
                    }
                }
            }
        }

        val fightFragment = FightFragment(combatants, filteredList)

        childFragmentManager.beginTransaction()
            .replace(binding.herolistFragment.id, fightFragment)
            .commit()

        //Ocultando los elementos del recycler view
        binding.heroList.visibility = View.INVISIBLE
    }

    private val viewModel: HeroListViewModel by activityViewModels()
    private val filteredList = mutableListOf<Hero>()
    var numberOfHeroesAlive: Int = 2

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentHeroListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.heroLiveData.observe(viewLifecycleOwner){
            when(it){
                is HeroListViewModel.HeroListState.Success -> {
                    filterHeros(it.heroList)
                    //adapter.updateList(it.heroList)
                }
                is HeroListViewModel.HeroListState.Error -> {
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                }
            }
        }
        viewModel.getHeroList()

        // RECIBE LA LISTA DE HEROES DESPUES DE UNA PELEA
        viewModel.updateHeroListAfterFight.observe(viewLifecycleOwner){
            binding.heroList.visibility = View.VISIBLE
            numberOfHeroesAlive = adapter.numberOfHeroesLiving
            Log.d("Heroes Alive", numberOfHeroesAlive.toString())
            Log.d("HeroListAfterFight", it.toString())
            createRecycler(it)
        }


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

   //FILTRO PARA QUITAR DE LA LISTA LOS PERSONAJES QUE NO SON DE DRAGON BALL
    private fun filterHeros(heroes: List<Hero>){
        for(heroIndex in heroes.indices){
            if(!heroes[heroIndex].photo.contains("<")&&
                !heroes[heroIndex].name.contains("starry") &&
                !heroes[heroIndex].name.contains("Quake")){
                if(heroes[heroIndex].name == "Broly"){
                    heroes[heroIndex].photo = "https://www.deanime.info/wp-content/uploads/2018/10/broly-edicion-pelicula-dragon-ball-super-2018-720x380.png"
                }
                filteredList.add(heroes[heroIndex])
            }
        }
        createRecycler(filteredList)
    }

    private fun createRecycler(list: List<Hero>){
        adapter.updateList(list)
        binding.heroList.adapter = adapter
        binding.heroList.layoutManager = LinearLayoutManager(requireContext())
    }
}