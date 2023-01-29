package com.keepcoding.androidfundamentos.ui.login

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import com.keepcoding.androidfundamentos.R
import com.keepcoding.androidfundamentos.databinding.ActivityLoginBinding
import com.keepcoding.androidfundamentos.ui.herolist.HeroListFragment

class LoginActivity : AppCompatActivity() {
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityLoginBinding

    private val viewModel: LoginViewModel by viewModels()
    companion object  {
        private var TAG_USERNAME: String = ""
        private var TAG_PASSWORD: String = ""
        var TOKEN: String = ""
        var isUserLoggedIn = false
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //setSupportActionBar(binding.toolbar)

        binding.username.doAfterTextChanged {
            TAG_USERNAME = binding.username.text.toString()
            binding.login.isEnabled = TAG_PASSWORD.isNotEmpty()and(TAG_USERNAME.isNotEmpty()and(TAG_USERNAME.contains("@")and(TAG_PASSWORD.length>=3)))
        }
        binding.password.doAfterTextChanged {
            TAG_PASSWORD = binding.password.text.toString()
            binding.login.isEnabled = TAG_PASSWORD.isNotEmpty()and(TAG_USERNAME.isNotEmpty()and(TAG_USERNAME.contains("@")and(TAG_PASSWORD.length>=3)))
        }

        binding.login.setOnClickListener{
            binding.loginProgressbar.visibility = View.VISIBLE

            viewModel.loginLiveData.observe(this){
                when(it){
                    is LoginViewModel.LoginState.Success ->{
                        TOKEN = it.token
                        val heroListFragment = HeroListFragment()
                        val fragment: Fragment
                        supportFragmentManager.findFragmentById(R.id.hero_list)
                        supportFragmentManager.beginTransaction()
                            .replace(R.id.container,heroListFragment)
                            .commit()

                        binding.login.visibility = View.INVISIBLE
                        binding.loginProgressbar.visibility = View.INVISIBLE
                        binding.password.visibility = View.INVISIBLE
                        binding.username.visibility = View.INVISIBLE
                    }
                    is LoginViewModel.LoginState.Error -> {
                        Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
                    }
                }
            }
            viewModel.login(TAG_USERNAME, TAG_PASSWORD)
        }
    }
}