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

        item = itemsCollection.find_one({"title":{"$regex": item, "$options":"i"}},{"_id":0})
        
        if not item:
            item = {}

        client.close()
        response = {"success":1, "item":item}
        return str(response)

    except pymongo.errors.ServerSelectionTimeoutError:
        response = {"success":0, "error":"Can not connect to database"}
        return str(response)
