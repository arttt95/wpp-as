package com.arttt95.whatsapp

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.arttt95.whatsapp.databinding.ActivityCadastroBinding
import com.arttt95.whatsapp.model.Usuario
import com.arttt95.whatsapp.utils.exibirMensagem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.firestore.FirebaseFirestore

class CadastroActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityCadastroBinding.inflate(layoutInflater)
    }

    private lateinit var nome: String
    private lateinit var email: String
    private lateinit var password: String

    private val firebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }

    private val firestore by lazy {
        FirebaseFirestore.getInstance()
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

        inicializarToolbar()
        inicializarEventosClique()

    }

    private fun validarCampos(): Boolean {

        nome = binding.editNome.text.toString()
        email = binding.editEmail.text.toString()
        password = binding.editPassword.text.toString()

        if(nome.isNotEmpty()) {
            binding.textInputLayoutNome.error = null
            if(email.isNotEmpty()) {
                binding.textInputLayoutEmail.error = null
                if(password.isNotEmpty()) {
                    binding.textInputLayoutPassword.error = null
                    return true
                } else {
                    binding.textInputLayoutPassword.error = "Preencha a sua senha"
                    return false
                }
            } else {
                binding.textInputLayoutEmail.error = "Preencha o seu e-mail"
                return false
            }
        } else {
            binding.textInputLayoutNome.error = "Preencha o seu nome"
            return false
        }

    }

    private fun inicializarEventosClique() {

        binding.btnCadastrar.setOnClickListener { 
            
            if(validarCampos()) {
                cadastrarUsuario(nome, email, password)
            }
            
        }

    }

    private fun cadastrarUsuario(nome: String, email: String, password: String) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { resultado ->
                if(resultado.isSuccessful) {

                    // Salvar os dados do usuário no Firestore
                    // id, nome, email, foto
                    val idUsuario = resultado.result.user?.uid
                    if(idUsuario != null) {
                        val usuario = Usuario(
                            idUsuario, nome, email,
                        )
                        salvarUsuarioFirestore(usuario)
                    }

                }
            }.addOnFailureListener { erro ->
                try {
                    throw erro
                } catch (erroUsuarioExistente: FirebaseAuthUserCollisionException) {
                    erroUsuarioExistente.printStackTrace()
                    exibirMensagem("E-mail já cadastrado")
                } catch (erroWeakPassword: FirebaseAuthWeakPasswordException) {
                    erroWeakPassword.printStackTrace()
                    exibirMensagem("Senha muito fraca, tente com letras, números e caractéres especiais")
                } catch (erroCredenciaisInvalidas: FirebaseAuthInvalidCredentialsException) {
                    erroCredenciaisInvalidas.printStackTrace()
                    exibirMensagem("E-mail inválido, digite um outro e-mail")
                }
            }
    }

    private fun salvarUsuarioFirestore(usuario: Usuario) {

        firestore
            .collection("usuarios")
            .document(usuario.id)
            .set(usuario)
            .addOnSuccessListener {
                exibirMensagem("Cadastro realizado com sucesso")
            }.addOnFailureListener { exception ->
                exibirMensagem("Erro ao tentar realizar cadastro. Code -> ${exception.message}")
            }

        startActivity(
            Intent(applicationContext, MainActivity::class.java)
        )

    }

    private fun inicializarToolbar() {
        val toolbar = binding.includeToolbar.tbPrincipal
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            title = "Cadastro"
            setDisplayHomeAsUpEnabled(true)
        }
    }

}