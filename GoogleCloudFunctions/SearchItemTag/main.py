import logging
import base64
import textwrap
import pymongo


def main(request):

    #Check item

    itemName = 'item'
    item = None

    request_json = request.get_json()

    if request.args and itemName in request.args:
        item = request.args.get(itemName)
    elif request_json and itemName in request_json:
        item = request_json[itemName]

    if not item:
        response = {"success":0, "error":"Please pass a "+ itemName +" on the query string or in the request body"}
        return str(response)

    item, code = searchItem(item)

    if item is None and code == 1:
            response = {"success":0, "description":"Can not find item"}
    elif item == None and code == 0:
        response = {"success":0, "error":"Can not connect to database"}
    else:
        response = {"success":1, "item":item, "size":len(item)}

    return str(response)



def searchItem(item):

    host = "35.230.149.72"
    user = "mongoUser"
    password = "MadridView1234"
    madridViewDBName = "madridViewDB"
    uri = "mongodb://"+user+":"+password+"@"+host+"/"+madridViewDBName
 
    client = pymongo.MongoClient(uri)
    itemsCollectionName = "Items"

    try:

        info = client.server_info()
        db = client[madridViewDBName]
        itemsCollection = db[itemsCollectionName]

        items = []
        names = {}

        item = itemsCollection.find_one({"title":{"$regex": item, "$options":"i"}},{"_id":0, "relation":0, "address":0, "organization":0})

        client.close()

        return item, 1

    except pymongo.errors.ServerSelectionTimeoutError:
        return None, 0
