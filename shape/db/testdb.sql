select a.FTR_IDN, a.FTR_CDE, b.geometry, NVL(a.FTR_STR, ' ') as FTR_STR from WTL_PIPE_LM a,  G_WTL_PIPE_LM b where a.FTR_IDN=b.FTR_IDN and  a.FTR_CDE = 'SA001' and (((b.minx between 329385 and 330369 or b.maxx between 329385 and 330369) or (b.miny between 510015 and 510505 or b.maxy between 510015 and 510505)) or ((329385 between b.minx and b.maxx or 330369 between b.minx and b.maxx) or (510015 between b.miny and b.maxy or 510505 between b.miny and b.maxy)))