package com.arttt95.whatsapp

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.arttt95.whatsapp.databinding.ActivityLoginBinding
import com.arttt95.whatsapp.utils.exibirMensagem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException

class LoginActivity : AppCompatActivity() {

    private lateinit var email: String
    private lateinit var password: String

    private val binding by lazy {
        ActivityLoginBinding.inflate(layoutInflater)
    }

    private val firebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        inicializarEventosClique()
        firebaseAuth.signOut()

    }

    override fun onStart() {
        super.onStart()
        verificarUsuarioLogado()
    }

    private fun verificarUsuarioLogado() {
        val usuarioAtual = firebaseAuth.currentUser
        if(usuarioAtual != null) {
            startActivity(Intent(this, MainActivity::class.java))
        }
    }


    private fun inicializarEventosClique() {

        binding.textCadastro.setOnClickListener {
            startActivity(
                Intent(this, CadastroActivity::class.java)
            )
        }

        binding.btnLogar.setOnClickListener {
            if(validarCampos()) {
                logarUsuario()
            }
        }

    }

    private fun logarUsuario() {
        firebaseAuth.signInWithEmailAndPassword(
            email, password
        ).addOnSuccessListener {
            exibirMensagem("Login efetuado com sucesso")
            startActivity(Intent(this, MainActivity::class.java))
        }.addOnFailureListener { erro ->
            try {
                throw erro
            } catch (erroUsuarioInvalido: FirebaseAuthInvalidUserException) {
                exibirMensagem("E-mail não cadastrado")
                binding.textInputLayoutLoginEmail.error = "E-mail incorreto"
            } catch (erroSenhaInvalida: FirebaseAuthInvalidCredentialsException) {
                exibirMensagem("E-mail ou senha incorretos")
                binding.textInputLayoutLoginPassword.error = "Senha incorreto"
            }
        }



    }

    private fun validarCampos(): Boolean {

        email = binding.editLoginEmail.text.toString()
        password = binding.editLoginPassword.text.toString()

        if(email.isNotEmpty()) {
            binding.textInputLayoutLoginPassword.error = null

            if (password.isNotEmpty()) {
                binding.textInputLayoutLoginPassword.error = null
                return true
            } else {
                binding.textInputLayoutLoginPassword.error = "Insira a senha"
                return false
            }

        } else {
            binding.textInputLayoutLoginEmail.error = "Preencha o e-mail"
            return false
        }

    }

}