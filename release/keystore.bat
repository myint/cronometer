@echo off
echo *** to generate 'jws' keystore ***
echo password: ??????
echo name: spaz.ca
echo organizational unit: spaz.ca
echo organization: spaz.ca
echo city / locality: Dublin
echo state / province: Dublin
echo country code: ie
keytool -genkey -keystore jws -alias spaz.ca
