package com.istic_pokemon.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.istic_pokemon.R
import com.istic_pokemon.databinding.ItemPokemonBinding
import com.istic_pokemon.models.Pokemon

class PokemonAdapter(
    private var pokemonList: List<Pokemon>,
    private val onItemClick: (Pokemon) -> Unit
) : RecyclerView.Adapter<PokemonAdapter.PokemonViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PokemonViewHolder {
        val binding = ItemPokemonBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PokemonViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PokemonViewHolder, position: Int) {
        val pokemon = pokemonList[position]
        holder.bind(pokemon)
    }

    override fun getItemCount(): Int = pokemonList.size

    fun updateList(newList: List<Pokemon>) {
        this.pokemonList = newList
        notifyDataSetChanged()
    }

    inner class PokemonViewHolder(
        private val binding: ItemPokemonBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(pokemon: Pokemon) {

            binding.pokemonName.text = pokemon.name.replaceFirstChar { it.uppercase() }

            binding.tvPokemonType.text = "Tipo: Desconocido"

            val imageUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/${getPokemonId(pokemon.url)}.png"
            Glide.with(binding.root.context)
                .load(imageUrl)
                .placeholder(R.drawable.ic_pokeball)
                .error(R.drawable.ic_pokeball)
                .into(binding.ivPokemonIcon)

            binding.root.setOnClickListener {
                onItemClick(pokemon)
            }
        }

        private fun getPokemonId(url: String): String {
            return url.split("/").filter { it.isNotEmpty() }.last()
        }
    }
}