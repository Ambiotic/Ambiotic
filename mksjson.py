
import os
import re
import json
from pprint import PrettyPrinter

config = {
    "dirmap"   : { "sounds" : "amb" },
    "root"     : "./src/main/resources/assets/ambiotic/sounds",
    "skipdirs" : ["biome", "birds", "bugs", "wolf"],
    "jsonfile" : "./src/main/resources/assets/ambiotic/sounds.json"
}

def dircs_name_space(dirc):
    dirc = os.path.basename(os.path.normpath(dirc))
    if dirc in config["dirmap"] :
        return config["dirmap"][dirc]
    return dirc

def get_ogg_grouping(ogg):
    name = os.path.basename(os.path.normpath(ogg))
    match = re.match(r'([^0-9]+)(\d*)\.ogg$', name)
    return match.group(1)

def get_mc_path(ogg):
    return ogg.replace(config["root"]+"/","")



def build_name_spaces(root, dirc, sounds={}):
    if root != "":
        root += "."
    for item in os.listdir(dirc):
        abspth = dirc+"/"+item
        if os.path.isfile(abspth):
            group = root+get_ogg_grouping(item)
            if not group in sounds:
                sounds[group] = { "category" : "ambient", "sounds" : [] }
            sound = get_mc_path(abspth)
            sound = sound.replace(".ogg","")
            if not "inst_" in item:
                sound = { "name" : sound }
                sound["stream"] = True
            sounds[group]["sounds"].append(sound)
        elif not item in config['skipdirs']:
            sounds = build_name_spaces(item,abspth,sounds)
    return sounds


sjson = json.dumps(build_name_spaces("amb", config["root"]), indent=4, sort_keys=True)
jfile = open(config['jsonfile'],"w")
jfile.write(sjson)
jfile.close()




