package com.arttt95.whatsapp

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.arttt95.whatsapp.databinding.ActivityPerfilBinding
import com.arttt95.whatsapp.utils.exibirMensagem

class PerfilActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityPerfilBinding.inflate(layoutInflater)
    }

    private val gerenciadorGaleria = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        if(uri != null) {
            binding.imgPerfil.setImageURI( uri )
        } else {
            exibirMensagem("Nenhuma imagem selecionada")
        }
    }

    private var hasPermitionCamera = false
    private var haspermitionGaleria = false

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
        solicitarPermissoes()
        inicializarEventosClique()

    }

    private fun inicializarEventosClique() {

        binding.fabSelecionar.setOnClickListener {

            if( haspermitionGaleria ) {
                gerenciadorGaleria.launch("image/*")
            } else {
                exibirMensagem("Sem permissão de Galeria")
                solicitarPermissoes()
            }

        }

    }

    private fun solicitarPermissoes() {

        // Verificar se o usuário já tem a permissão
        hasPermitionCamera = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            haspermitionGaleria = ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_MEDIA_IMAGES
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            haspermitionGaleria = ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        }

        // LISTA DE PERMISSÕES NEGADAS
        val listaPermissoesNegadas = mutableListOf<String>()

        if(!hasPermitionCamera) {
            listaPermissoesNegadas.add(Manifest.permission.CAMERA)
        }

        if(!haspermitionGaleria) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                listaPermissoesNegadas.add(Manifest.permission.READ_MEDIA_IMAGES)
            } else {
                listaPermissoesNegadas.add(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }

        if(listaPermissoesNegadas.isNotEmpty()) {

            // Solicitar permissões multiplas
            val gerenciadorPermissoes = registerForActivityResult(
                ActivityResultContracts.RequestMultiplePermissions()
            ){ permissoes ->

                hasPermitionCamera = permissoes[Manifest.permission.CAMERA]
                    ?: hasPermitionCamera

                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    haspermitionGaleria= permissoes[Manifest.permission.READ_MEDIA_IMAGES]
                        ?: haspermitionGaleria
                } else {
                    haspermitionGaleria= permissoes[Manifest.permission.READ_EXTERNAL_STORAGE]
                        ?: haspermitionGaleria
                }

            }

            gerenciadorPermissoes.launch(listaPermissoesNegadas.toTypedArray())

        }

    }

    private fun inicializarToolbar() {
        val toolbar = binding.includeToolbarPerfil.tbPrincipal
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            title = "Perfil"
            setDisplayHomeAsUpEnabled(true)
        }
    }

}