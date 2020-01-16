# Melita

Suite of tools for degenerate primer development (Jaric et al, 2013).

This suite of tools includes eight analysis pipelines:
AmplStat: Produces statistics on a sequence database, specifically genera that are missing/present, their counts, and number of species
DBStat: Outputs more general statistics, including unique genera/species, unique sequences, and distinguishing taxa
DegPrimerTree: Produces a set of degenerate primers, using the taxon-specific algorithm of (Jaric et al, 2013)
DPDesign: Produces a set of possible forward and reverse primers for an input set of sequences
DPStats: Produces a list of taxa/sequences that would be successfully amplified during PCR given a forward and reverse primer pair
Extract16S: Extract the 16S region from an input sequence set
ReadAmplSet: Outputs unique reads
Test: Tests some core features of Melita

Melita is under open-source MIT License and any professional work that uses Melita should cite:

M. Jaric, J. Segal, E. Silva-Herzong, L. Schneper, K. Mathee and G. Narasimhan. Better Primer Design for Metagenomics Applications By Increasing Taxonomic Distinguishability.  BMC Proceedings 8(S7):S4, 2013.

All current questions should be directed to lead developer Trevor Cickovski (tcickovs@fiu.edu).

Melita is released in memory of Melita Jaric.
