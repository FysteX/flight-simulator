2D flight simulator.

Application consists of 2D map, menu for adding airports and flights, saving them and reading them from CSV file, list of all added airports, and operations with simulation (start, pause, restart the simulation).

By adding airport, it is shown on map as gray square and in list of all airports, airports can be selected by clicking on them, and user can toggle visibility of airport on map with checkbox next to airport 
description in the list of airports.
Flight consists of informations about starting airport, ending airport, flight duration and taking off time.
Simulation starts at 00:00 and lasts 24 hours where 1 second in real time is 10 minutes in simulation. Flights are simulated by blue circle representing airplane. They take off from starting airport
when timer reaches scheduled take off time and airplane is moving towards ending aiirport for the flight duration. Only 1 airplane can take off in one 10 minute period, so if multiple airplanes want to take off
in same 10 minute period, they are added in waiting queue waiting for next free 10 minute period.
