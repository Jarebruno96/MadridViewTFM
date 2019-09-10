import pymongo

def main(request):

    usrName = 'userName'
    userName = None

    request_json = request.get_json()

    #Check longitude

    if request.args and usrName in request.args:
        userName = request.args.get(usrName)
    elif request_json and usrName in request_json:
        userName = request_json[usrName]

    if not userName:
        response = {"success":0, "error":"Please pass a "+ usrName +" on the query string or in the request body"}
        return str(response)

    favourites, code = getFavouriteItems(userName)

    if code != 0:
        response = {"success":0, "error":"Internal server error searching items"}
        return str(response)
    
    response = {"success": 1, "favourites":favourites, "size":len(favourites)}
    return str(response)

def getFavouriteItems(userName):

    host = "35.230.149.72"
    user = "mongoUser"
    password = "MadridView1234"
    madridViewDBName = "madridViewDB"
    uri = "mongodb://"+user+":"+password+"@"+host+"/"+madridViewDBName
 
    client = pymongo.MongoClient(uri)
    favouritesCollectionName = "Favourites"

    try:

        info = client.server_info()
        db = client[madridViewDBName]
        favouritesCollection = db[favouritesCollectionName]

        favourites = []

        for favourite in favouritesCollection.find({"userName":userName},{"_id":0, "userName":0}):
            favourites.append(favourite["favourites"])

        client.close()

        return favourites[0], 0

    except pymongo.errors.ServerSelectionTimeoutError as e:
        print("Can not connect to mongo database")
        return None, -1
