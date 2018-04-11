package com.el.robot.calculator.services.factories;

import com.el.robot.calculator.services.BetCalculator;
import com.el.robot.calculator.services.FundCalculator;
import com.el.robot.calculator.services.OddsCalculator;
import com.el.robot.calculator.services.OddsShareCalculator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

@SuppressWarnings("unchecked")
@Component
public class ServiceFactory {

    @Autowired
    private List<BetCalculator> betCalculatorList;

    @Autowired
    private List<FundCalculator> fundCalculatorList;

    @Autowired
    private List<OddsCalculator> oddsCalculatorList;

    @Autowired
    private List<OddsShareCalculator> oddsShareCalculatorList;

    private Map<Class, List<Object>> serviceByModelMap = new HashMap<>();

    @PostConstruct
    public void registerBettingService() {

        List<Object> serviceInstanceList = new ArrayList<>();
        serviceInstanceList.addAll(betCalculatorList);
        serviceInstanceList.addAll(fundCalculatorList);
        serviceInstanceList.addAll(oddsCalculatorList);
        serviceInstanceList.addAll(oddsShareCalculatorList);

        for (Object serviceInstance : serviceInstanceList) {
            //ignore derivatives
            if(serviceInstance.getClass().getGenericInterfaces().length == 0) {
                continue;
            }

            Type type = serviceInstance.getClass().getGenericInterfaces()[0];
            if (type instanceof ParameterizedType) {
                Class modelType = (Class) ((ParameterizedType) type).getActualTypeArguments()[0];

                List<Object> serviceInstances;
                if (serviceByModelMap.containsKey(modelType)) {
                    serviceInstances = serviceByModelMap.get(modelType);
                } else {
                    serviceInstances = new ArrayList<>();
                    serviceByModelMap.put(modelType, serviceInstances);
                }

                serviceInstances.add(serviceInstance);
            }
        }
    }

    public boolean containsService(Class modelType) {
        return serviceByModelMap.containsKey(modelType);
    }

    public boolean containsService(Class modelType, Class serviceType) {
        if (!serviceByModelMap.containsKey(modelType)) {
            return false;
        }

        List<Object> objects = serviceByModelMap.get(modelType);
        return objects.stream().anyMatch((object) -> serviceType.isAssignableFrom(object.getClass()));
    }

    private  <T> Optional<T> getSubService(Class modelType, Class serviceType) {
        for (Map.Entry<Class, List<Object>> serviceEntry : serviceByModelMap.entrySet()) {
            if(serviceEntry.getKey().isAssignableFrom(modelType)) {
                Optional<T> service = getService(serviceEntry.getValue(), serviceType);
                if(service.isPresent()) {
                    return service;
                }
            }
        }

        return Optional.empty();

//        return (T) serviceByModelMap.entrySet().parallelStream()
//                .filter(serviceEntry -> serviceEntry.getKey().isAssignableFrom(modelType))
//                .filter(serviceEntry -> serviceEntry.getValue().contains(modelType))
//                .map(serviceEntry -> getService(serviceEntry.getValue(), serviceType))
//                .findFirst().get();
    }

    public <T> T getService(Class modelType, Class<T> serviceType) {
        if (!containsService(modelType, serviceType)) {
            //try to get sub service
            Optional<Object> subService = getSubService(modelType, serviceType);
            if(!subService.isPresent()) {
                throw new RuntimeException("Service for " + modelType.getSimpleName() + " hasn't been found!!!");
            }

            return (T) subService.get();
        }

        List<Object> objects = serviceByModelMap.get(modelType);
        return (T) getService(objects, serviceType).get();
    }

    public <T> Optional<T> getService(List<Object> objects, Class serviceType) {
        return (Optional<T>) objects.stream().filter((object) -> serviceType.isAssignableFrom(object.getClass())).findFirst();
    }

}
