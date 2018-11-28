select a.FTR_IDN, a.FTR_CDE, b.geometry, NVL(a.FTR_STR, ' ') as FTR_STR from WTL_FIRE_PS a,  G_WTL_FIRE_PS b where a.FTR_IDN=b.FTR_IDN and  a.FTR_CDE = 'SA119' and (((b.minx between 329391 and 331358 or b.maxx between 329391 and 331358) or (b.miny between 510020 and 511000 or b.maxy between 510020 and 511000)) or ((329391 between b.minx and b.maxx or 331358 between b.minx and b.maxx) or (510020 between b.miny and b.maxy or 511000 between b.miny and b.maxy)));

desc WTL_FIRE_PS;

select a.FTR_IDN, a.FTR_CDE, b.geometry, NVL(a.FTR_STR, ' ') as FTR_STR, NVL(a.PUR_NAM, ' ') as PUR_NAM from WTL_PURI_AS a,  G_WTL_PURI_AS b where a.FTR_IDN=b.FTR_IDN and  a.FTR_CDE = 'SA113' and (((b.minx between 329391 and 331358 or b.maxx between 329391 and 331358) or (b.miny between 510020 and 511000 or b.maxy between 510020 and 511000)) or ((329391 between b.minx and b.maxx or 331358 between b.minx and b.maxx) or (510020 between b.miny and b.maxy or 511000 between b.miny and b.maxy)));

select max(ftr_idn)+1 from WTL_PURI_AS;

select * from WTL_PURI_AS;

select * from G_WTL_PURI_AS;

select * from G_WTL_PURI_AS where ftr_idn=2;

delete from G_WTL_PURI_AS where ftr_idn=2;

select * from WTL_PIPE_LM where ftr_idn=1128;

select * from G_WTL_PIPE_LM where ftr_idn=1127;

delete from G_WTL_PIPE_LM where ftr_idn=1127;

select * from G_WTL_PIPE_LM;


select a.FTR_IDN, a.FTR_CDE, b.geometry, NVL(a.FTR_STR, ' ') as FTR_STR from WTL_PIPE_LM a,  G_WTL_PIPE_LM b where a.FTR_IDN=b.FTR_IDN and  a.FTR_CDE = 'SA001' and (((b.minx between 330293 and 331277 or b.maxx between 330293 and 331277) or (b.miny between 510334 and 510824 or b.maxy between 510334 and 510824)) or ((330293 between b.minx and b.maxx or 331277 between b.minx and b.maxx) or (510334 between b.miny and b.maxy or 510824 between b.miny and b.maxy)));

select * from WTL_FIRE_PS;

select * from G_WTL_FIRE_PS;


select geometry.sdo_gtype   from G_WTL_PURI_AS;

select a.FTR_IDN, a.FTR_CDE, b.geometry, NVL(a.FTR_STR, ' ') as FTR_STR from WTL_FIRE_PS a,  G_WTL_FIRE_PS b where a.FTR_IDN=b.FTR_IDN and  a.FTR_CDE = 'SA119' and (((b.minx between 329391 and 331358 or b.maxx between 329391 and 331358) or (b.miny between 510020 and 511000 or b.maxy between 510020 and 511000)) or ((329391 between b.minx and b.maxx or 331358 between b.minx and b.maxx) or (510020 between b.miny and b.maxy or 511000 between b.miny and b.maxy)));

