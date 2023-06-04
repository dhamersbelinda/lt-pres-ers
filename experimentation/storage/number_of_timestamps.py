import matplotlib.pyplot as plt
from matplotlib.ticker import (MultipleLocator, AutoMinorLocator)
from matplotlib import rc
import pandas as pd
import numpy as np
import math
import sys
sys.setrecursionlimit(2000)

# Use LaTeX rendering (requires installing MiKTeX on windpws + ghostscript)
# Makes the rendering slower tho
plt.rc('text', usetex=True)
# Use LaTeX's default 'Computer Modern' font for labels as well
plt.rc('font', family='serif')

docs_per_day = 512
number_of_days_per_cycle = 365
number_of_cycles = 5

B = [2, 3] # shapes
L = [128, 256, 512] # colors, TODO change this

color_cycle = plt.rcParams['axes.prop_cycle'].by_key()['color']
stronger_cycle = ['midnightblue', 'red', 'darkolivegreen', 'darkred', 'indigo']
linestyles = ['solid', 'dashed', 'dotted']
line_widths = [1, 3]
dashes = [(5,0), (5,10)]

df_table = [[0 for y in range(len(L))] for x in range(len(B))]

for b in range(len(B)):
    for l in range(len(L)):
        df_table[b][l] = pd.read_csv('storage_B{}_L{}_Req{}.txt'.format(B[b], L[l], docs_per_day))

def num_ts_classic(time):
    time = time + 1
    sum = 0
    full_periods = time // number_of_days_per_cycle
    for i in range(2, full_periods + 2):
        sum = sum + i * number_of_days_per_cycle * docs_per_day
    sum = sum + (time - full_periods * number_of_days_per_cycle) * docs_per_day
    return sum

def num_ts_pred(time, l):
    complete_cycles = time // number_of_days_per_cycle
    sum = 0
    to_monitor = 0
    ts_per_cycle = number_of_days_per_cycle * math.ceil(docs_per_day/l)
    
    for cycle in range(complete_cycles):
        sum = sum + ts_per_cycle + math.ceil((ts_per_cycle + to_monitor)/l)
        to_monitor = math.ceil((ts_per_cycle + to_monitor)/l)
        
    added = (time - complete_cycles*number_of_days_per_cycle)*math.ceil(docs_per_day/l)
    sum = sum + added
    to_monitor = to_monitor + added
    return sum

def monitor(time, la):
    if time == 0:
        return math.ceil(docs_per_day/la)
    else:
        if ((time + 1) % number_of_days_per_cycle) == 0:
            return math.ceil((monitor(time-1,la) + math.ceil(docs_per_day/la))/la)
        else:
            return monitor(time-1, la) + math.ceil(docs_per_day/la)
        
def monitor_classic(time):
    return time*docs_per_day
        
def num_pred_rec(time, la):
    if time == 0:
        return monitor(0,la)
    else:
        if(not isinstance(time, int)):
            print(type(time))
        if ((time + 1) % number_of_days_per_cycle) == 0:
            return num_pred_rec(time-1,la) + math.ceil(docs_per_day/la) + monitor(time, la)
        else:
            return num_pred_rec(time-1,la) + math.ceil(docs_per_day/la)

t = range(number_of_days_per_cycle*number_of_cycles)
classic_num = list(map(num_ts_classic, t))


######### Number of timestamps + comparison ###############
fig, axs = plt.subplots(1, 2, figsize=(10,5.5))
# fig.suptitle("Total number of timestamps")

# ax[0] comparison classic vs worst
b = 0
l = 0
root_count = df_table[b][l]['root_count']
axs[0].plot(t, root_count, label="B = {}, L = {}".format(B[b], L[l]), color=color_cycle[l], linestyle=linestyles[b])
axs[0].set_xlabel(r'time in number of days',fontsize=19)
axs[0].set_ylabel(r'Number of timestamps',fontsize=19)
axs[0].tick_params(labelsize=15)
axs[0].plot(t, list(map(num_ts_classic, t)), 'm', label="classic", color=color_cycle[4])
axs[0].set_xlim(0, t[-1])
axs[0].legend(ncol=1, loc='upper left', fontsize=15, framealpha=0.9)
axs[0].grid(True, color='gray', alpha=0.75, linestyle='-', linewidth=0.3)
axs[0].set_xticks(np.arange(0,365*5+1,365))
axs[0].xaxis.set_major_locator(MultipleLocator(365))
axs[0].xaxis.set_minor_locator(MultipleLocator(91.25))
# axs[1].yaxis.set_major_locator(MultipleLocator(0.2e9))
# axs[1].yaxis.set_minor_locator(MultipleLocator(0.05e9))
axs[0].set_title(r'Comparison with classic augmentation')

# ax[1] comparison all B and L
for b in range(len(B)):
    for l in range(len(L)):
        root_count = df_table[b][l]['root_count']
        axs[1].plot(t, root_count, label="B = {}, L = {}".format(B[b], L[l]), color=color_cycle[l], linestyle=linestyles[b], linewidth=line_widths[b], dashes=dashes[b])
axs[1].set_xlabel(r'time in number of days',fontsize=19)
axs[1].set_ylabel(r'Number of timestamps',fontsize=19)
axs[1].tick_params(labelsize=15)
axs[1].set_xlim(0, t[-1])
axs[1].legend(ncol=1, loc='upper left', fontsize=15, framealpha=0.9)
axs[1].grid(True, color='gray', alpha=0.75, linestyle='-', linewidth=0.3)
axs[1].set_xticks(np.arange(0,365*5+1,365))
axs[1].set_title(r'Comparison with different $B$ and $L$')
axs[1].xaxis.set_major_locator(MultipleLocator(365))
axs[1].xaxis.set_minor_locator(MultipleLocator(91.25))
# axs[1].yaxis.set_major_locator(MultipleLocator(0.2e9))
# axs[1].yaxis.set_minor_locator(MultipleLocator(0.05e9))

fig.tight_layout()

plt.show()

######### Number of timestamps vs. predictions ###############
fig, axs = plt.subplots(1, 1, figsize=(10,5.5))
# fig.suptitle("Real number of timestamps vs. prediction")

b = 0 # because independent of branching factor
for l in range(len(L)):
    root_count = df_table[b][l]['root_count']
    lam = lambda x : num_pred_rec(x, L[l])
    axs.plot(t, root_count, label=r'Real: $L = {}$'.format(L[l]), color=color_cycle[l])
    axs.plot(t, list(map(lam, t)), label=r'Predicted: $L = {}$'.format(L[l]), color=stronger_cycle[l], linewidth=3, linestyle='--', dashes=(5, 10))
# for x in [i*number_of_days_per_cycle for i in range(number_of_cycles)]:
#     axs.axvline(x, color='r', linestyle="dotted")
axs.set_xlabel(r'time in number of days',fontsize=19)
axs.set_ylabel(r'Number of timestamps',fontsize=19)
axs.tick_params(labelsize=15)
axs.set_xlim(0, t[-1])
axs.grid(True, color='gray', alpha=0.75, linestyle='-', linewidth=0.3)
axs.set_xticks(np.arange(0,365*5+1,365))
axs.xaxis.set_major_locator(MultipleLocator(365))
axs.xaxis.set_minor_locator(MultipleLocator(91.25))
# axs[1].yaxis.set_major_locator(MultipleLocator(0.2e9))
# axs[1].yaxis.set_minor_locator(MultipleLocator(0.05e9))
axs.legend(ncol=1, loc='upper left', fontsize=15, framealpha=0.9)

fig.tight_layout()
plt.show()

# ######### Number of monitored timestamps : predictions vs classic ###############
fig, axs = plt.subplots(1, 2, figsize=(10,5.5))
# fig.suptitle("Number of timestamps to monitor")

b = 0 # because independent of branching factor


for l in range(len(L)):
    root_count = df_table[b][l]['root_count']
    lam = lambda x : monitor(x, L[l])
    y = list(map(lam, t))
    print(y)
    for ax in range(2):
        axs[ax].plot(t, y, label=r'$L = {}$'.format(L[l]), color=color_cycle[l])

axs[0].plot(t, list(map(monitor_classic,t)), label='classic', color=color_cycle[4])

for ax in range(2):
    axs[ax].set_xlabel(r'time in number of days',fontsize=19)
    axs[ax].set_ylabel(r'Number of monitored timestamps',fontsize=19)
    axs[ax].tick_params(labelsize=15)
    axs[ax].set_xlim(0, t[-1])
    axs[ax].set_xticks(np.arange(0,365*5+1,365))
    axs[ax].grid(True, color='gray', alpha=0.75, linestyle='-', linewidth=0.3)
    axs[ax].legend(ncol=1, loc='upper left', fontsize=15, framealpha=0.9)
    axs[ax].xaxis.set_major_locator(MultipleLocator(365))
    axs[ax].xaxis.set_minor_locator(MultipleLocator(91.25))
    # axs[ax].yaxis.set_major_locator(MultipleLocator(0.2e9))
    # axs[ax].yaxis.set_minor_locator(MultipleLocator(0.05e9))

fig.tight_layout()
plt.show()

