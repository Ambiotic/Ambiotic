function vclmp(volume) {
    if(volume < 0.0)
        return 0.0;
    else if(volume > 1.0)
        return 1.0;
    return volume;
}

function biomecheck(biomes, biome) {
    biome = biome.toLowerCase();
    for(i = 0; i < biomes.length; i++) {
        if(biome.indexOf(biomes[i].toLowerCase()) != -1) {
            return true;
        }
    }
    return false;
}
