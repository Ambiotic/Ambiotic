function vclmp(volume) {
    if(volume < 0.0)
        return 0.0;
    else if(volume > 1.0)
        return 1.0;
    return volume;
}

function inlist(haystack, needle) {
    for(i = 0; i < haystack.length; i++) {
        if(haystack[i] === needle) {
            return true;
        }
    }
    return false;
}

