package io.github.willena.connect.influxdb.providers;

import org.apache.kafka.connect.sink.SinkRecord;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Collectors;

public class ProviderChain implements Provider {

    List<Provider> providerList;

    public ProviderChain(String prefix, Map<String, String> settings) {

        if (settings.containsKey(prefix + ".providers")) {
            //Multiple providers situation
            providerList = Arrays.stream(settings.get(prefix + ".providers").split(","))
                    .map(name -> {
                        String className = settings.get(prefix + ".providers." + name + ".class");

                        try {
                            return (Provider) Class.forName(className).getDeclaredConstructor(String.class, Map.class).newInstance(prefix + ".providers." + name, settings);
                        } catch (ClassNotFoundException | InvocationTargetException | InstantiationException |
                                 IllegalAccessException | NoSuchMethodException e) {
                            throw new RuntimeException(e);
                        }

                    }).collect(Collectors.toList());
        } else if (settings.containsKey(prefix + ".provider.class")) {
            //Single provider !
            try {
                providerList = Collections.singletonList((Provider) Class.forName(settings.get(prefix + ".provider.class")).getDeclaredConstructor(String.class, Map.class).newInstance(prefix + ".provider", settings));
            } catch (ClassNotFoundException | InstantiationException |
                     IllegalAccessException | NoSuchMethodException e) {
                throw new RuntimeException(e);
            } catch (InvocationTargetException e) {
                throw new RuntimeException(e.getCause());
            }
        } else {
            // Static provider !
            providerList = Collections.singletonList(new Static(prefix, settings));
        }

    }

    @Override
    public Map<String, Object> get(SinkRecord r) {
        return providerList.stream().map(provider -> provider.get(r)).reduce(new HashMap<>(), (stringStringMap, stringStringMap2) -> {
            stringStringMap.putAll(stringStringMap2);
            return stringStringMap;
        });
    }
}
