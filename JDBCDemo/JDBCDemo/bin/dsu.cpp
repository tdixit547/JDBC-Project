#include<bits/stdc++.h>
using namespace std;
class DisjointSetUnion{
    vector<int>parent,rank;
    public:
    DisjointSetUnion(int n){
        parent.resize(n+1,0);
        rank.resize(n+1);
        for(int i= 1; i<=n; i++){
            parent[i] = i;
        }
    }
    int find(int u){
        if(parent[u]==u){
            return u;
        }
        return parent[u] = find(parent[u]);
    }
    void unionByRank(int u, int v){
        int pu = find(u);
        int pv = find(v);
        if(pu==pv){
            return;
        }
        if(rank[pu]<rank[pv]){
            parent[pu] = pv;
        }
        else if(rank[pv]<rank[pu]){
            parent[pv] = pu;
        }
        else{
            parent[pv] = pu;
            rank[pu]++;
        }
    }
}