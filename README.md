2D flight simulator.

Application consists of 2D map, menu for adding aerports and flights, saving them and reading them from CSV file, list of all added aerports, and operations with simulation (start, pause, stop).

By adding aerport, it is shown on map as gray square and in list of all aerports, aerports can be selected by clicking on them, and user can toggle visibility of aerport on map with checkbox next to aerport 
description in the list of aerports.
Flight consists of informations about starting aerport, ending aerport, flight duration and taking off time.
Simulation starts at 00:00 and lasts 24 hours where 1 second in real time is 10 minutes in simulation. Flights are simulated by blue circle representing airplane. They take off from starting aerport
when timer hits their take off time and airplane is moving towards ending aerport for the flight duration. Only 1 airplane can take of in one 10 minute period, so if multiple airplanes want to take off
in same 10 minute period, they are added in waiting queue waiting for next free 10 minute period.
