# Game of Three

Gameplay is to divide the number by 3 until one player reaches 1

When a player starts, it incepts a random (whole) number and sends it to the second player as an approach to 
starting the game. The receiving player can now always choose between adding one of {-1, 0, 1} to get to a number 
that is divisible by 3. Divide it by three. The resulting whole number is then sent back to the original sender.
The same rules are applied until one player reaches the number 1

Both players can play either in AUTOMATIC mode or MANUAL mode (user input numbers). This is configurable from the UI.

More info  
[To Client](./game-of-three-client/README.md)  
[To Server](./game-of-three-server/README.md)

### How to run
- You can use docker-compose to run both services in containers
- `cd to project root`
- `docker-compose -f ./docker-compose.yaml build`
- `docker-compose -f ./docker-compose.yaml up`