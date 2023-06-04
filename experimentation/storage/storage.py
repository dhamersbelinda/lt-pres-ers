import requests
import psycopg2
import numpy as np
from threading import Thread
from multiprocessing.pool import ThreadPool
from concurrent.futures import ThreadPoolExecutor
from time import perf_counter;

NUM_LOAD = 512 # number of PreservePO calls before build tree
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

json_data_build = {
    'reqId': '0',
}

json_data_renew = { # with new poids
    'reqId': '2',
}

sql_query = "SELECT pg_total_relation_size('nodes') AS Nodes, pg_total_relation_size('root') AS Roots, pg_total_relation_size('poids') AS POID, pg_database_size('er_test') AS Db_size;"
sql_query_node_count = "SELECT count(*) as Node_count from nodes;"
sql_query_root_count = "SELECT count(*) as Root_count from root;"
sql_query_poid_count = "SELECT count(*) as Poid_count from poids;"

def task(i):
    # print("!", end="")
    requests.post('http://localhost:8080/pres/PreservePO', headers=headers, json=json_data_load)

B = 5
L = 512
req = 512

try:
    connection = psycopg2.connect(database="er_test",
                        host="localhost",
                        user="postgres",
                        password="postgres",
                        port="5432",
                        options="-c search_path=er_test_schema")
    cursor = connection.cursor()

    with open('storage_B{}_L{}_Req{}.txt'.format(B, L, req), 'w') as f:
        print("nodes_table_size,root_table_size,poids_table_size,db_size,node_count,root_count,poids_count,tree_build_time", file = f)

        for run in range(NUM_RUNS):
            for day in range(NUM_DAYS):
                time1 = perf_counter()
                threads = []
                # for load in range(NUM_LOAD):
                #     thread = Thread(target=task)
                #     threads.append(thread)
                #     thread.start()
                # for load in range(NUM_LOAD):
                #     threads[load].join()
                # NUM_PROCESSES = 8
                # pool = ThreadPool(processes=NUM_PROCESSES)
                #for i in range(int(NUM_LOAD/NUM_PROCESSES)):
                    #for j in range(NUM_PROCESSES):
                # inputs = [None] * NUM_LOAD
                # result = pool.map_async(task, range(NUM_LOAD))
                # output = result.wait()


                for load in range(NUM_LOAD):
                    requests.post('http://localhost:8080/pres/PreservePO', headers=headers, json=json_data_load)

                print("Day "+ str(day) + ": " + "time for " + str(NUM_LOAD) + " requests: " + str(perf_counter() - time1))
                time2 = perf_counter()
                requests.post('http://localhost:8080/pres/ValidateEvidence', headers=headers, json=json_data_build)
                time3 = perf_counter()

                # daily printing
                cursor.execute(sql_query)
                size_list = cursor.fetchall()
                for row in size_list:
                    for value in row:
                        print(str(value) + ",", end="", file=f)
                cursor.execute(sql_query_node_count)
                size_list = cursor.fetchall()
                for row in size_list:
                    for value in row:
                        print(str(value) + ",", end="", file=f)
                cursor.execute(sql_query_root_count)
                size_list = cursor.fetchall()
                for row in size_list:
                    for value in row:
                        print(str(value) + ",", end="", file=f)
                cursor.execute(sql_query_poid_count)
                size_list = cursor.fetchall()
                for row in size_list:
                    for value in row:
                        print(str(value) + ",", end="", file=f)
                print(str(time3-time2), file=f)

            with open('storage_B{}_L{}_Req{}_renewal.txt'.format(B, L, req), 'a') as v:
                time4 = perf_counter()
                requests.post('http://localhost:8080/pres/ValidateEvidence', headers=headers, json=json_data_renew)
                time5 = perf_counter()
                print(str(time5-time4), file=v)
    

    

except (Exception, psycopg2.Error) as error:
    print("Error while fetching data from PostgreSQL", error)

finally:
    if connection:
        cursor.close()
        connection.close()
        print("PostgreSQL connection is closed")





# Note: json_data will not be serialized by requests
# exactly as it was in the original request.
# data = '{\n  "pro": "https://uclouvain.be/en/faculties/epl/preservation-api/profile/v1.0",\n  "po": [\n    {\n      "binaryData": {\n        "value": "eyJkaWdBbGciOiIyLjE2Ljg0MC4xLjEwMS4zLjQuMi4xIiwiZGlnVmFsIjpbIityeXRQeEZFS0pZSENvQlBXbStteVNuVzlndnhlcm9NSVk5MTM3bE1pKzQ9Il19"\n      },\n      "formatId": "http://uri.etsi.org/19512/format/DigestList"\n    }\n  ]\n}'
# response = requests.post('http://localhost:8080/pres/PreservePO', headers=headers, data=data)