if EXIST "conf.properties" (
    for /F "tokens=*" %%I in (conf.properties) do set %%I
)