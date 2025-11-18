import os
import json
import tkinter as tk
import copy
from tkinter.filedialog import askdirectory

def updateModels(path, oldPath):
    if (not os.path.isdir(oldPath)):
        os.mkdir(oldPath)
    for file in os.listdir(path):
        filepath = os.path.join(path, file)
        if (os.path.isfile(filepath)):
            name, extension = os.path.splitext(filepath)
            if (extension != ".json"):
                continue
            
            with open(filepath, "r") as f:
                originalData = json.load(f)
            with open(os.path.join(oldPath, os.path.basename(filepath)), "w") as f:
                json.dump(originalData, f, indent=2)
            
            version = 0
            if ("version" in originalData):
                version = originalData["version"]
            newData = originalData
            if (version == 0):
                newData = modelV0toV1(filepath, newData)
                version = 1
            newData["version"] = version
            with open(filepath, "w") as f:
                json.dump(newData, f, indent=2)
                
        if (os.path.isdir(filepath)):
            dirname = os.path.dirname(filepath)
            updateModels(filepath, os.path.join(oldDir, dirname))

def modelV0toV1(name, json):
    newJson = {}
    try:
        newJson["animations"] = json["animations"]
        newJson["textureSize"] = json["textureSize"]
        parts = []
        for part in json["parts"]:
            parts.append(updateV0Part(part))
        newJson["parts"] = parts
    except Exception as e:
        print(f"An unexpected error occurred on {name}: {e}")
    return newJson
def updateV0Part(part):
    newPart = {}
    partType = "databank:group"
    if (part["isCube"]):
        partType = "databank:cube"
    if (part["isMesh"]):
        partType = "databank:mesh"
    newPart["type"] = partType
    newPart["name"] = part["name"]
    if (partType == "databank:group"):
        newPart["rotation"] = part["rotation"]
        newPart["offset"] = part["offset"]
        newPart["children"] = []
        if ("children" in part):
            for child in part["children"]:
                newPart["children"].append(updateV0Part(child))
    if (partType == "databank:cube"):
        newPart["rotation"] = part["rotation"]
        newPart["offset"] = part["offset"]
        newPart["texOffset"] = part["texOffset"] if "texOffset" in part else [ 0, 0 ]
        newPart["mirror"] = part["mirror"] if "mirror" in part else False
        newPart["origin"] = part["origin"] if "origin" in part else [ 0, 0, 0 ]
        newPart["dimensions"] = part["dimensions"] if "dimensions" in part else [ 1, 1, 1 ]
        newPart["inflate"] = part["inflate"] if "inflate" in part else 0
    if (partType == "databank:mesh"):
        newPart["rotation"] = part["rotation"]
        newPart["offset"] = part["offset"]
        faces = []
        vertices = {}
        for face in part["faces"]:
            newFace = []
            for vertex in face:
                vertexId = str(len(vertices))
                newVertex = {}
                newVertex["x"] = vertex["x"]
                newVertex["y"] = vertex["y"]
                newVertex["z"] = vertex["z"]
                newVertex["weights"] = {}
                vertices[vertexId] = newVertex
                newVertRef = {}
                newVertRef["id"] = vertexId
                newVertRef["u"] = vertex["u"]
                newVertRef["v"] = vertex["v"]
                newFace.append(newVertRef)
            faces.append(newFace)
        newPart["faces"] = faces
        newPart["vertices"] = vertices
    return newPart

while (True):
    print("")
    print("What would you like to do?")
    print("1. Model Update")
    print("x. Exit")

    choice = input().lower()

    if (choice == "x"):
        break

    if (choice == "1"):
        print("Please select your models folder.")
        print("Note: It will recursively update jsons.")
        path = askdirectory(title='Please select your models folder.')
        print("Please select a folder to dump old jsons into.")
        oldPath = askdirectory(title='Please select a folder to dump old jsons into.')
        if (path == ''):
            print("Failed to select directory: Empty Path")
            continue

        updateModels(path, oldPath)

