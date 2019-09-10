import pymongo
import mpu

def main(request):
    """Responds to any HTTP request.
    Args:
        request (flask.Request): HTTP request object.
    Returns:
        The response text or any set of values that can be turned into a
        Response object using
        `make_response <http://flask.pocoo.org/docs/1.0/api/#flask.Flask.make_response>`.
    """

    lonName = 'longitude'
    latName = 'latitude'
    distName = 'distance'
    searchMuseumName = 'searchMuseum'
    searchMonumentName = 'searchMonument'
    searchParkName = 'searchPark'
    searchChurchName = 'searchChurch'
    searchTouristOfficeName = 'searchTouristOffice'

    
    longitude = latitude = distance = None

    request_json = request.get_json()

    #Check longitude

    if request.args and lonName in request.args:
        longitude = request.args.get(lonName)
    elif request_json and lonName in request_json:
        longitude = request_json[lonName]

    if longitude:
        try:
            longitude = float(longitude)
        except ValueError as e:
            response = {"success":0, "error":lonName+" is not in the correct format!"}
            return str(response)
    else:
        response = {"success":0, "error":"Please pass a "+ lonName +" on the query string or in the request body"}
        return str(response)


    #Check latitude
    
    if request.args and latName in request.args:
        latitude = request.args.get(latName)
    elif request_json and latName in request_json:
        latitude = request_json[latName]

    if latitude:
        try:
            latitude = float(latitude)
        except ValueError as e:
            response = {"success":0, "error":latName+" is not in the correct format!"}
            return str(response)
    else:
        response = {"success":0, "error":"Please pass a "+ latName +" on the query string or in the request body"}
        return str(response)


    #Check distance
    
    if request.args and distName in request.args:
        distance = request.args.get(distName)
    elif request_json and distName in request_json:
        distance = request_json[distName]

    if distance:
        try:
            distance = float(distance)
        except ValueError as e:
            response = {"success":0, "error":distName+" is not in the correct format!"}
            return str(response)
    else:
        response = {"success":0, "error":"Please pass a "+ distName +" on the query string or in the request body"}
        return str(response)

    
    #Check searchMuseum
    
    if request.args and searchMuseumName in request.args:
        searchMuseum = request.args.get(searchMuseumName)
    elif request_json and searchMuseumName in request_json:
        searchMuseum = request_json[searchMuseumName]

    if searchMuseum:
        try:
            searchMuseum = int(searchMuseum)
        except ValueError as e:
            response = {"success":0, "error":searchMuseumName+" is not in the correct format!"}
            return str(response)
    else:
        response = {"success":0, "error":"Please pass a "+ searchMuseumName +" on the query string or in the request body"}
        return str(response)
    
    if searchMuseum==1:
        print("Busco museos")
    else:
        print("No busco museos")
    
    #Check searchMonument
    
    if request.args and searchMonumentName in request.args:
        searchMonument = request.args.get(searchMonumentName)
    elif request_json and searchMonumentName in request_json:
        searchMonument = request_json[searchMonumentName]

    if searchMonument:
        try:
            searchMonument = int(searchMonument)
        except ValueError as e:
            response = {"success":0, "error":searchMonumentName+" is not in the correct format!"}
            return str(response)
    else:
        response = {"success":0, "error":"Please pass a "+ searchMonumentName +" on the query string or in the request body"}
        return str(response)

    
    #Check searchPark
    
    if request.args and searchParkName in request.args:
        searchPark = request.args.get(searchParkName)
    elif request_json and searchParkName in request_json:
        searchPark = request_json[searchParkName]

    if searchPark:
        try:
            searchPark = int(searchPark)
        except ValueError as e:
            response = {"success":0, "error":searchParkName+" is not in the correct format!"}
            return str(response)
    else:
        response = {"success":0, "error":"Please pass a "+ searchParkName +" on the query string or in the request body"}
        return str(response)
    
    
    #CheckTouristOffice
    
    if request.args and searchTouristOfficeName in request.args:
        searchTouristOffice = request.args.get(searchTouristOfficeName)
    elif request_json and searchTouristOfficeName in request_json:
        searchTouristOffice = request_json[searchTouristOfficeName]

    if searchTouristOffice:
        try:
            searchTouristOffice = int(searchTouristOffice)
        except ValueError as e:
            response = {"success":0, "error":searchTouristOfficeName+" is not in the correct format!"}
            return str(response)
    else:
        response = {"success":0, "error":"Please pass a "+ searchTouristOfficeName +" on the query string or in the request body"}
        return str(response)
    
    #CheckChurch
    
    if request.args and searchChurchName in request.args:
        searchChurch = request.args.get(searchChurchName)
    elif request_json and searchChurchName in request_json:
        searchChurch = request_json[searchChurchName]

    if searchChurch:
        try:
            searchChurch = int(searchChurch)
        except ValueError as e:
            response = {"success":0, "error":searchChurchName+" is not in the correct format!"}
            return str(response)
    else:
        response = {"success":0, "error":"Please pass a "+ searchChurchName +" on the query string or in the request body"}
        return str(response)

    items, code = searchCloseItems(longitude, latitude, distance, searchMonument, searchMuseum, searchPark, searchTouristOffice, searchChurch)

    if code != 0:
        response = {"success":0, "error":"Internal server error searching items"}
        return str(response)
    
    response = {"success": 1, "items":items, "size":len(items)}
    return str(response)

def searchCloseItems(longitude, latitude, distance, searchMonument, searchMuseum, searchPark, searchTouristOffice, searchChurch):

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
        #db.Items.find({"type":{"$in":["Monumento", "Parque"]}}).count();
        types = []
        
        if searchMonument==1:
            types.append("Monumento")
            
        if searchMuseum==1:
            types.append("Museo") 
            
        if searchChurch==1:
            types.append("Iglesia católica")  
            
        if searchPark==1:
            types.append("Parque")   
            
        if searchTouristOffice==1:
            types.append("Oficina de Turismo")

        for item in itemsCollection.find({"type":{"$in":types}},{"_id":0, "relation":0, "address":0, "organization":0}):
            
            if 'location' in item:
                if (mpu.haversine_distance((longitude, latitude), (item['location']['longitude'], item['location']['latitude']))*1000) <= distance:
                    items.append(item)

        client.close()

        return items, 0

    except pymongo.errors.ServerSelectionTimeoutError as e:
        print("Can not connect to mongo database")
        return None, -1



