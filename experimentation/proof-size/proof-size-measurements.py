import requests
import psycopg2
import numpy as np
import base64
import time

NUM_LOAD = 4000 # number of PreservePO calls before build tree
NUM_DAYS = 365  # number of "days" before Renewal
NUM_RUNS = 5    # number of renewal cycles

ts_size = 100

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

json_data_build = { # with new poids
    'reqId': '0',
}

json_data_build_renew = { # new poids + renewals
    'reqId': '2',
}

json_retrieve = {'poId':'364758bd-7c97-453b-93cc-16e636464c05'}

B = 4
L_min = 1
L_max = 1024
L_step = 1

f = open("proof_size_B{}_L{}-{}-{}".format(B,L_min,L_max,L_step), "w")
f.write('L proof_size\n')
poids = [None] * L_max
poid = None
# size = []
for L in np.arange(L_step,L_max+L_step,L_step):
    print("L={}".format(L))

    # f.write('{} '.format(L))
    start = time.time()
    preserve = requests.post('http://localhost:8080/pres/PreservePO', headers=headers, json=json_data_load)
    if(preserve.status_code != 200):
        print("ERROR NOT 200")
        exit()
    poid = preserve.json()['poId']
    if L != 1:
        resp = requests.post('http://localhost:8080/pres/ValidateEvidence', headers=headers, json={'reqId': "5 {}".format(L-1)})
        if(resp.status_code != 200):
            print("ERROR NOT 200")
            exit()
    # for i in range(L):
    #     preserve = requests.post('http://localhost:8080/pres/PreservePO', headers=headers, json=json_data_load)
    #     if(preserve.status_code != 200):
    #         print("ERROR NOT 200")
    #         exit()
    #     if(i > L/2 or L <= 2):
    #         poid = preserve.json()['poId']
    end = time.time()
    print("PreservePO : {:.6} sec".format(end-start))

    # Build a tree
    start = time.time()
    requests.post('http://localhost:8080/pres/ValidateEvidence', headers=headers, json=json_data_build)
    end = time.time()
    print("BuildTree : {:.6} sec".format(end-start))

    # json_preserve = json={'poId':poids[int(np.min([L-1,np.ceil(L/2)]))]}
    json_preserve = json={'poId':poid}

    start = time.time()
    retrieve = requests.post('http://localhost:8080/pres/RetrievePO', headers=headers,
                              json=json_preserve )
    end = time.time()
    print("RetrievePO : {:.6} sec".format(end-start))
    if(retrieve.status_code != 200):
        print("ERROR NOT 200")
        exit()

    f.write('{} {}\n'.format(L, len(base64.b64decode(retrieve.json()['po'][0]['xmlData']['b64Content'].encode('ascii')))))
    # for i in range(L):
    #     retrieve = requests.post('http://localhost:8080/pres/RetrievePO', headers=headers, json={'poId':poids[i]})
    #     # if(i==0):
    #     #     print(base64.b64decode(retrieve.json()['po'][0]['xmlData']['b64Content'].encode('ascii')))
    #     f.write('{} '.format(len(base64.b64decode(retrieve.json()['po'][0]['xmlData']['b64Content'].encode('ascii')))))
    #     # size.append(len(base64.b64decode(retrieve.json()['po'][0]['xmlData']['b64Content'].encode('ascii'))))

    # f.write("\n")
    # size.clear()


# print(preserve.json()['poId'])
# json_retrieve = {'poId':preserve.json()['poId']}

# retrieve = requests.post('http://localhost:8080/pres/RetrievePO', headers=headers, json=json_retrieve)
# print(retrieve)
# print(len(base64.b64decode(retrieve.json()['po'][0]['xmlData']['b64Content'].encode('ascii'))))

f.close()
# try:
    # connection = psycopg2.connect(database="er_test",
    #                     host="localhost",
    #                     user="postgres",
    #                     password="postgres",
    #                     port="5432",
    #                     options="-c search_path=er_test_schema")
    # cursor = connection.cursor()

    # for run in range(NUM_RUNS):
    #     for day in range(NUM_DAYS):
    #         for load in range(NUM_LOAD):
    #             response = requests.post('http://localhost:8080/pres/PreservePO', headers=headers, json=json_data_load)
    #         response = requests.post('http://localhost:8080/pres/ValidateEvidence', headers=headers, json=json_data_build)
    #         cursor.execute(sql_query)
    #     size_list = cursor.fetchall()
    #     for row in size_list:
    #         print(row)
    #     response = requests.post('http://localhost:8080/pres/ValidateEvidence', headers=headers, json=json_data_renew)
    

    

# except (Exception, psycopg2.Error) as error:
#     print("Error while fetching data from PostgreSQL", error)

# finally:
#     if connection:
#         cursor.close()
#         connection.close()
#         print("PostgreSQL connection is closed")





# Note: json_data will not be serialized by requests
# exactly as it was in the original request.
# data = '{\n  "pro": "https://uclouvain.be/en/faculties/epl/preservation-api/profile/v1.0",\n  "po": [\n    {\n      "binaryData": {\n        "value": "eyJkaWdBbGciOiIyLjE2Ljg0MC4xLjEwMS4zLjQuMi4xIiwiZGlnVmFsIjpbIityeXRQeEZFS0pZSENvQlBXbStteVNuVzlndnhlcm9NSVk5MTM3bE1pKzQ9Il19"\n      },\n      "formatId": "http://uri.etsi.org/19512/format/DigestList"\n    }\n  ]\n}'
# response = requests.post('http://localhost:8080/pres/PreservePO', headers=headers, data=data)