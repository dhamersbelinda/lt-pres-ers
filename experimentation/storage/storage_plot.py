import matplotlib.pyplot as plt
from matplotlib.ticker import (MultipleLocator, AutoMinorLocator)
import numpy as np
import pandas as pd
from matplotlib import rc
# Use LaTeX rendering (requires installing MiKTeX on windpws + ghostscript)
# Makes the rendering slower tho
plt.rc('text', usetex=True)
# Use LaTeX's default 'Computer Modern' font for labels as well
plt.rc('font', family='serif')

B = [2, 3] # shapes
L = [128, 256, 512] # colors
docs_per_day = 512
number_of_days_per_cycle = 365
number_of_cycles = 5

color_cycle = plt.rcParams['axes.prop_cycle'].by_key()['color']
stronger_cycle = ['midnightblue', 'red', 'darkolivegreen', 'darkred', 'indigo']
linestyles = ['solid', 'dashed', 'dotted']
line_widths = [1, 3]
dashes = [(5,0), (5,10)]

df_table = [[0 for y in range(len(L))] for x in range(len(B))]

for b in range(len(B)):
    for l in range(len(L)):
        df_table[b][l] = pd.read_csv('storage_B{}_L{}_Req{}.txt'.format(B[b], L[l], docs_per_day))
        
t = range(number_of_days_per_cycle*number_of_cycles)

################ Big overview ############################
content = ['_table_size'] #content = ['_count', '_table_size']
subjects = ['nodes', 'root'] #subjects = ['nodes', 'root', 'poids']
fig, axs = plt.subplots(len(content), len(subjects) + 1, figsize=(10,5.5))

for b in range(len(B)):
    for l in range(len(L)):
        axs[0].plot(df_table[b][l]['db_size'], label="B = {}, L = {}".format(B[b], L[l]), color=color_cycle[l], linestyle=linestyles[b], linewidth=line_widths[b] )
axs[0].set_xlim(0, t[-1])
axs[0].set_xlabel(r'time in number of days',fontsize=15)
axs[0].set_ylabel(r'Size [bytes]',fontsize=15)
axs[0].legend(ncol=1, loc='upper left', framealpha=0.9)
axs[0].grid(True, color='gray', alpha=0.75, linestyle='-', linewidth=0.3)
axs[0].set_xticks(np.arange(0,365*5+1,365))
axs[0].set_title(r'Database size')

for content_index in range(len(content)):
    for subject_index in range(len(subjects)):
        for b in range(len(B)):
            for l in range(len(L)):
                axs[subject_index+1].plot(df_table[b][l][subjects[subject_index]+content[content_index]], label="B = {}, L = {}".format(B[b], L[l]), color=color_cycle[l], linestyle=linestyles[b], linewidth=line_widths[b] )
        axs[subject_index+1].set_xlim(0, t[-1])
        axs[subject_index+1].set_xlabel(r'time in number of days',fontsize=15)
        axs[subject_index+1].set_ylabel(r'Size [bytes]',fontsize=15)
        axs[subject_index+1].legend(ncol=1, loc='upper left', framealpha=0.9)
        axs[subject_index+1].grid(True, color='gray', alpha=0.75, linestyle='-', linewidth=0.3)
        axs[subject_index+1].set_xticks(np.arange(0,365*5+1,365))
axs[1].set_title(r'Nodes table size')
axs[2].set_title(r'Root table size')

fig.tight_layout()
plt.show()


############## Comparing table size with multiplication of row counts ###########################
# field size
# compare size of node table with number_of_nodes * size_of_row to get a realistic view of situation

size_dict = {
    'bigint' : 8,
    'uuid' : 16,
    'digest_method' : 23,
    'boolean' : 1,
    'ts_with_tz' : 8,
    'digest_value': 33,
    'ts' : 2628.5
}

nodes_row_real_size = 4*size_dict['bigint'] + size_dict['digest_value'] + 24
poids_row_real_size = size_dict['uuid'] + 3*size_dict['bigint'] + size_dict ['digest_method'] + size_dict['digest_value'] + size_dict['ts_with_tz'] + 24
root_row_real_size = 2*size_dict['bigint'] + size_dict['ts_with_tz'] + size_dict['boolean'] + size_dict['digest_method'] + size_dict['ts'] + 24

b = 0   # B = 2
l = 2 # L = 512

# nodes

node_quotients = [0]*len(t)
for time in t:
    value = df_table[b][l]['nodes_table_size'].iloc[time]/df_table[b][l]['nodes_count'].multiply(nodes_row_real_size).iloc[time]
    node_quotients[time] = value

nodes_quotient = np.average(node_quotients)
print(nodes_quotient)


# root

root_quotients = [0]*len(t)
for time in t:
    value = df_table[b][l]['root_table_size'].iloc[time]/df_table[b][l]['root_count'].multiply(root_row_real_size).iloc[time]

    root_quotients[time] = value

root_quotient = np.average(root_quotients)
print(root_quotient)


# poids

poids_quotients = [0]*len(t)
for time in t:
    value = df_table[b][l]['poids_table_size'].iloc[time]/df_table[b][l]['poids_count'].multiply(poids_row_real_size).iloc[time]

    poids_quotients[time] = value

poids_quotient = np.average(poids_quotients)
print(poids_quotient)




###### Comparison real row count with predicted row count for a model with classic augmentation #####################

poids_row_pred_size = size_dict['uuid'] + 2*size_dict['bigint'] + size_dict ['digest_method'] + size_dict['digest_value'] + 24
root_row_pred_size = size_dict['uuid'] + 2*size_dict['ts_with_tz'] + size_dict['boolean'] + size_dict['digest_method'] + size_dict['ts'] + 24

def num_ts_classic(time):
    time = time + 1
    sum = 0
    full_periods = time // number_of_days_per_cycle
    for i in range(2, full_periods + 2):
        sum = sum + i * number_of_days_per_cycle * docs_per_day
    sum = sum + (time - full_periods * number_of_days_per_cycle) * docs_per_day
    return sum

classic_ts_num = list(map(num_ts_classic, t))
# poids number is the same in all cases
classic_poids_num = df_table[b][l]['poids_count']

poids_rows_real_size = df_table[b][l]['poids_count'].multiply(poids_row_real_size)
poids_rows_pred_size = df_table[b][l]['poids_count'].multiply(poids_row_pred_size)

root_rows_real_size = df_table[b][l]['root_count'].multiply(root_row_real_size)
root_rows_pred_size = np.array(classic_ts_num)*root_row_pred_size

nodes_rows_real_size = df_table[b][l]['nodes_count'].multiply(nodes_row_real_size)

# Preuve que le modèle avec la constante se tient
fig, axs = plt.subplots(1, 1, figsize=(10,5.5))
# nodes
axs.plot(t, df_table[b][l]['nodes_table_size'], label=r'nodes table size', color=color_cycle[0])
axs.plot(t, nodes_rows_real_size.multiply(nodes_quotient), label=r'nodes approximation', color=stronger_cycle[0], linewidth=line_widths[1], linestyle=linestyles[1], dashes=dashes[1])

# root
axs.plot(t, df_table[b][l]['root_table_size'], label=r'root table size', color=color_cycle[1])
axs.plot(t, root_rows_real_size.multiply(root_quotient), label=r'root approximation', color=stronger_cycle[1], linewidth=line_widths[1], linestyle=linestyles[1], dashes=dashes[1])

# poid
axs.plot(t, df_table[b][l]['poids_table_size'], label=r'poids table size', color=color_cycle[2])
axs.plot(t, poids_rows_real_size.multiply(poids_quotient), label=r'poids approximation', color=stronger_cycle[2], linewidth=line_widths[1], linestyle=linestyles[1], dashes=dashes[1])

axs.set_xlabel(r'time in number of days',fontsize=19)
axs.set_ylabel(r'Size [bytes]',fontsize=19)
axs.tick_params(labelsize=15)
axs.set_xlim(0, t[-1])
axs.legend(ncol=1, loc='upper left', fontsize=15, framealpha=0.9)
axs.grid(True, color='gray', alpha=0.75, linestyle='-', linewidth=0.3)
axs.set_xticks(np.arange(0,365*5+1,365))
# axs.set_title(r'Table size linear approximation comparison with B = {} and L = {}'.format(B[b], L[l]))

axs.xaxis.set_major_locator(MultipleLocator(365))
axs.xaxis.set_minor_locator(MultipleLocator(73))

axs.yaxis.set_major_locator(MultipleLocator(0.5e8))
axs.yaxis.set_minor_locator(MultipleLocator(0.25e8))

fig.tight_layout()
plt.show()


## Proportion of the three tables to db_size
table_size_sum = df_table[b][l]['nodes_table_size'] + df_table[b][l]['root_table_size'] + df_table[b][l]['poids_table_size']




###############################################################################

# plt.plot(t, table_size_sum, label="Sum of real table sizes", color='r')

total_real_rows_sum = poids_rows_real_size.multiply(poids_quotient) + root_rows_real_size.multiply(root_quotient) + nodes_rows_real_size.multiply(nodes_quotient)
# plt.plot(t,total_real_rows_sum, label="Sum of all rows with coefficients" )

total_pred_rows_sum = root_rows_pred_size*root_quotient + poids_rows_pred_size.multiply(poids_quotient)
# plt.plot(t,total_pred_rows_sum, label="Sum of prediction rows with coefficients" )
# plt.plot(t, table_size_sum, label="Real db size")

# plt.legend()
# plt.show()


# Comparison of predicted sum of table sizes with sum of real table sizes
fig, axs = plt.subplots(1, 2, figsize=(10,5.5))

axs[0].plot(t, total_pred_rows_sum, label=r"Classic augmentation", color='black')
axs[0].plot(t, table_size_sum, label=r"Sum of table sizes with ERs", color=color_cycle[3])
axs[0].set_xlabel(r'time in number of days',fontsize=19)
axs[0].set_ylabel(r'Size [bytes]',fontsize=19)
axs[0].tick_params(labelsize=15)
axs[0].set_xlim(0, t[-1])
axs[0].legend(ncol=1, loc='upper left', fontsize=15, framealpha=0.9)
axs[0].grid(True, color='gray', alpha=0.75, linestyle='-', linewidth=0.3)
axs[0].set_xticks(np.arange(0,365*5+1,365))
axs[0].set_title(r'Table size sums with B = {} and L = {}'.format(B[b], L[l]))
axs[0].xaxis.set_major_locator(MultipleLocator(365))
axs[0].xaxis.set_minor_locator(MultipleLocator(91.25))
axs[0].yaxis.set_major_locator(MultipleLocator(0.2e10))
axs[0].yaxis.set_minor_locator(MultipleLocator(0.1e10))

axs[1].plot(t, df_table[b][l]['nodes_table_size'], label=r"Nodes table size")
axs[1].plot(t, df_table[b][l]['root_table_size'], label=r"Root table size")
axs[1].plot(t, df_table[b][l]['poids_table_size'], label=r"Poids table size")
axs[1].plot(t, table_size_sum, label=r"Sum of table sizes with ERs", dashes=(5,1))
axs[1].plot(t, df_table[b][l]['db_size'], label=r"Total database size (ER)", dashes=(5,3))
axs[1].set_xlabel(r'time in number of days',fontsize=19)
# axs[1].set_ylabel(r'Size [bytes]',fontsize=19)
axs[1].tick_params(labelsize=15)
axs[1].set_xlim(0, t[-1])
axs[1].legend(ncol=1, loc='upper left', fontsize=15, framealpha=0.9)
axs[1].grid(True, color='gray', alpha=0.75, linestyle='-', linewidth=0.3)
axs[1].set_xticks(np.arange(0,365*5+1,365))
axs[1].set_title(r'Table sizes with B = {} and L = {}'.format(B[b], L[l]))
axs[1].xaxis.set_major_locator(MultipleLocator(365))
axs[1].xaxis.set_minor_locator(MultipleLocator(91.25))
axs[1].yaxis.set_major_locator(MultipleLocator(0.2e9))
axs[1].yaxis.set_minor_locator(MultipleLocator(0.05e9))

fig.tight_layout()

plt.show()


# montrer comment ça varie avec B et L
# montrer que c'est principalement roots qui influe, même s'il y a bcp plus de nodes
# montrer le stockage par rapport au classique