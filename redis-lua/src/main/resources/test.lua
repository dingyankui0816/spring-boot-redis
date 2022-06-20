local exist = redis.call("EXISTS",KEYS[1]);
if(exist == 0)
then return -1;
end;
return redis.call("INCRBY",KEYS[1],KEYS[2]);