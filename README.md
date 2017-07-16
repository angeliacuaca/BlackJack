# BlackJack

Mini console game replicate black jack. Both Programs generate communication.txt and gaming.txt for logging

SinglePlayer: Dealer holds the deck of cards and deal the card upon request from server. Object referring are done by Serializable method

Instruction:
  1. Run GameServer
  2. Run CommunicationLog 
  3. Run GamingLog
  4. Run Dealer
  5. Run Player


MultiPlayer: Instead of Object being referred like SinglePlayer, data output input stream
being pass through parameter for each Users implementing Runnable. Server holds the deck, but Players must ask server to request Dealer
thread

Instruction:
  1. Run GameServer
  2. Run CommunicationLog 
  3. Run GamingLog
  4. Run GameClient
  
GameClient represents dealer and 5 players.
- Sequence I’m using for multiplayer is not synchronised

Rules:
  Max card in hand each user is 10
  No error handling for maximum connection for Multiplayer
  No error handling for disconnected user in the middle of the game but
the game will still running with the null player.

