import math
import numpy as np
import pandas as pd
import matplotlib.pyplot as plt
import matplotlib as mpl
from matplotlib.ticker import (MultipleLocator, AutoMinorLocator)
from matplotlib import rc
import os

# Use LaTeX rendering (requires installing MiKTeX on windows + ghostscript)
# Makes the rendering slower tho
plt.rc('text', usetex=True)
# Use LaTeX's default 'Computer Modern' font for labels as well
plt.rc('font', family='serif')

# Storage approximations
# All in Bytes
def ers_storage(n_docs, B, overhead_per_node=64+20, ts_size=2628):
    L = np.ceil(np.emath.logn(B, n_docs))+1
    tot_nodes = (B*L - 1)/(B - 1)
    return tot_nodes * overhead_per_node + ts_size

def classic_storage(n_docs, ts_size=2628):
    return n_docs * ts_size

def hash_tree_size(B, L):
    return np.ceil(np.emath.logn(B, L)) * (B-1)

def reduced_hash_tree_size(B, L):
    return np.ceil(np.emath.logn(B, L)) * (B-1)
    # return np.emath.logn(B, L) * (B-1)

data = pd.read_csv("proof_size_renewal_d10_renewal10", sep=' ', index_col=False)

data2 = data[data['B']==2]
data3 = data[data['B']==3]
data5 = data[data['B']==5]
data10 = data[data['B']==10]
data20 = data[data['B']==20]

fig = plt.figure(figsize=(10,5.5))
ax = fig.add_subplot(1,1,1)

plt.plot(data2['renewal']+1, data2['size']/1000,  label=r"$B=2$", marker='o', markersize=4)
plt.plot(data3['renewal']+1, data3['size']/1000, label=r"$B=3$", marker='o', markersize=4)
plt.plot(data5['renewal']+1, data5['size']/1000, label=r"$B=5$", marker='o', markersize=4)
plt.plot(data10['renewal']+1, data10['size']/1000, label=r"$B=10$", marker='o', markersize=4)
plt.plot(data20['renewal']+1, data20['size']/1000, label=r"$B=20$", marker='o', markersize=4)
plt.plot(data20['renewal']+1, ((data2['renewal']+1)*2628)/1000, label=r"Classic (theoretical)", color='black', marker='o', markersize=4)

ax.set_xlabel(r'Number of renewals',fontsize=19)
ax.set_ylabel(r'Proof size [kilo Byte]',fontsize=19)

ax.tick_params(labelsize=15)
ax.xaxis.set_major_locator(MultipleLocator(1))

ax.yaxis.set_major_locator(MultipleLocator(10))
ax.yaxis.set_minor_locator(MultipleLocator(5))
# plt.xlim([-0.03 * x_lim, x_lim])

# plt.loglog()
plt.grid(True, color='gray', alpha=0.35, linestyle='-', linewidth=0.3)
plt.legend(ncol=2, loc='upper left', fontsize=15, framealpha=0.9)

fig.tight_layout()
plt.show()