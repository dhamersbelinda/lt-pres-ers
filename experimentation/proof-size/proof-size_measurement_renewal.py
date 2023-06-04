import requests
import psycopg2
import numpy as np
import math
import base64
import time

headers = {
    'accept': 'application/json',
    'Content-Type': 'application/json',
}
json_data_load = {
    'pro': 'https://uclouvain.be/en/faculties/epl/preservation-api/profile/v1.0',
    'po': [
        {
            'binaryData': {
                'value': 'eyJkaWdBbGciOiIyLjE2Ljg0MC4xLjEwMS4zLjQuMi4xIiwiZGlnVmFsIjpbIityeXRQeEZFS0pZSENvQlBXbStteVNuVzlndnhlcm9NSVk5MTM3bE1pKzQ9Il19',
            },
            'formatId': 'http://uri.etsi.org/19512/format/DigestList',
        },
    ],
}
full_clear_poids_query = "TRUNCATE TABLE poids CASCADE"
full_clear_nodes_query = "TRUNCATE TABLE nodes CASCADE"
# ==========================================

n_renewals = 10
Bs = [2,3,5,10,20]
d = 10 # 1024 leaves


f = open("proof_size_renewal_d{}_renewal{}".format(d, n_renewals), "w")
f.write('B renewal size value\n')

try:
    connection = psycopg2.connect(database="er_test",
                        host="localhost",
                        user="postgres",
                        password="postgres",
                        port="5432",
                        options="-c search_path=er_test_schema")
    connection.autocommit = True
    cursor = connection.cursor()

    for B in Bs:
        # L_max = B**d
        L_max = 1024
        cursor.execute(full_clear_poids_query)
        cursor.execute(full_clear_nodes_query)
        for r in range(n_renewals):
            print("renewal {}".format(r))
            if r== 0:
                preserve = requests.post('http://localhost:8080/pres/PreservePO', headers=headers, json=json_data_load)
                if(preserve.status_code != 200):
                    print("ERROR NOT 200")
                    exit()
                poid = preserve.json()['poId']
                print("POID: "+poid)
            # insert B**d -1 docs then build including renewals
            resp = requests.post('http://localhost:8080/pres/ValidateEvidence', headers=headers, json= {'reqId': "6 {} {} {} {}".format(2,B,L_max - 2,L_max)})
            if resp.status_code != 200:
                print("Error not 200 !")
                exit()
            
            # retrievePO and write to file
            retrieve = requests.post('http://localhost:8080/pres/RetrievePO', headers=headers,
                                    json = {'poId':poid} )
            if(retrieve.status_code != 200):
                print("ERROR NOT 200")
                exit()

            f.write('{} {} {} {}\n'.format(B, r,len(base64.b64decode(retrieve.json()['po'][0]['xmlData']['b64Content'].encode('ascii'))), base64.b64decode(retrieve.json()['po'][0]['xmlData']['b64Content'].encode('ascii'))))

except (Exception, psycopg2.Error) as error:
    print("Error while fetching data from PostgreSQL", error)

finally:
    f.close()
    if connection:
        cursor.close()
        connection.close()
        print("PostgreSQL connection is closed")
 


