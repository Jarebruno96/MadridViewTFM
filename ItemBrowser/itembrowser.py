import random
import logging
import urllib.request
import http.client, urllib.parse
import json
import pymongo
from googleapiclient import discovery

import settings as cfg

def googleCustomSearchImage(search):

    service = discovery.build("customsearch", "v1", developerKey=cfg.googleCustomSearchCfg["developerKey"])

    res = service.cse().list(
        q = search,
        cx = cfg.googleCustomSearchCfg["searchEngineId"],
        searchType = cfg.googleCustomSearchCfg["searchType"],
        num = cfg.googleCustomSearchCfg["num"],
        fileType = cfg.googleCustomSearchCfg["fileType"],
        safe = cfg.googleCustomSearchCfg["safe"]).execute()

    if not 'items' in res:
        return None
    else:
        for item in res['items']:
            return item['link']

def BingImageSearch(search):

    subscriptionKey = "9255cfea22b8447e9bc1d7d01e54e198"
    host = "api.cognitive.microsoft.com"
    path = "/bing/v7.0/images/search"
    count = 1

    headers = {'Ocp-Apim-Subscription-Key': subscriptionKey}
    conn = http.client.HTTPSConnection(host)
    query = urllib.parse.quote(search)
    conn.request("GET", path + "?q=" + query+"&count="+str(count), headers=headers)

    response = conn.getresponse()
    headers = [k + ": " + v for (k, v) in response.getheaders() if k.startswith("BingAPIs-") or k.startswith("X-MSEdge-")]

    return headers, response.read().decode("utf8")

def getElementOcupation():

    days = []

    for i in range(7):
        days.append(random.randrange(500, 1500))

    return days

def parseElementWithTemplate(element, template):

    result = {}

    for attr in template:

        if attr in element:

            if bool(template[attr]):
                result[attr] = parseElementWithTemplate(element[attr], template[attr])
            else:
                result[attr] = element[attr]

    return result

def itemsBrowser():

    urls = ["https://datos.madrid.es/egob/catalogo/208844-0-monumentos-edificios.json",
            "https://datos.madrid.es/egob/catalogo/201132-0-museos.json",
            "https://datos.madrid.es/egob/catalogo/206577-0-oficinas-turismo.json",
            "https://datos.madrid.es/egob/catalogo/200761-0-parques-jardines.json",
            "https://datos.madrid.es/egob/catalogo/209426-0-templos-catolicas.json",
            ]

    itemKinds = ["Monumento",
                "Museo",
                "Oficina de Turismo",
                "Parque",
                "Iglesia católica"]

    template = {"title":{},
                "relation":{},
                "location":{
                    "latitude":{},
                    "longitude":{}
                    },
                "address":{
                    "locality":{},
                    "postal-code":{},
                    "street-address":{}
                    },
                "organization":{
                        "organization-desc":{},
                        "organization-name":{},
                        "schedule":{},
                        "services":{}
                    }
                }
    madridViewDBName = "madridViewDB"
    client = pymongo.MongoClient("mongodb://"+cfg.mongoCfg["user"]+":"+cfg.mongoCfg["password"]+"@"+cfg.mongoCfg["host"]+"/"+madridViewDBName)
    itemsCollectionName = "Items"

    try:

        info = client.server_info()
        db = client[madridViewDBName]
        itemsCollection = db[itemsCollectionName]

        for url, itemKind in zip(urls, itemKinds):

            print("Searching "+itemKind)

            try:

                items = []
                response = urllib.request.urlopen(url)

                if response.getcode()==200:

                    html = response.read()
                    data = json.loads(html)

                    for element in data["@graph"]:

                        item = parseElementWithTemplate(element, template)
                        item["ocupation"] = getElementOcupation()

                        if itemsCollection.count_documents({"title":item["title"]}) == 0:

                            item["type"] = itemKind
                            item["image"] = ""

                            result = googleCustomSearchImage(item["title"])

                            if result is not None:
                                item["image"] = result

                            items.append(item)

                    if len(items) > 0:
                        itemsCollection.insert_many(items)


                else:
                    print("Something went wrong getting attributes")
            except Exception as e:
                raise e

        client.close()
        print("Function finished")

    except pymongo.errors.ServerSelectionTimeoutError:
        print("Can not connect to mongo database")

def main():
    itemsBrowser()

if __name__ == "__main__":
    main()
