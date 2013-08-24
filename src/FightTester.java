/*
 * 
 * NOTE: as attributes are added to monsters, items, etc. keep adjusting the copy constructors!
 * 
TODO category: bugs that need fixing

if a room is completely filled with monsters(and items, possibly) and the game tries to spawn a monster (or item) there, it crashes.
	*NOTE: this seems to be becoming more common as I add more generation methods. (might be confirmation bias though.)
	*this probably also happens if the whole level is full.
	*IDEA: have an "empty tile count" that increments and decrements during the level's creation, and
		 base the number of items/gold stacks on this value.
		 
if game starts taking a long time to start, consider adding invisible doors/tunnels only when the player visits the level, same as traps.

sometimes the screen "wiggles" (meaning everything in a row shifts right or left by one tile repeatedly) as a result of monster movement
	*this problem is rare, so it probably results from a very specific combination of tiles (like monsters standing on stairs out of view, or something.)
	
Resizing the window causes fields to overlap when they shouldn't.

occasionally when a monster spawns, other monsters will stop attacking the player.
	*this is subject to change as monster AI and hositility settings change. Keep an eye out for this bug.

have observed a stack overflow error when trying to pick up an item stack. not sure why. It seemed to happen when I had 85 arrows and tried to pick up 15, which makes me think it has to do with hitting the stack cap exactly.

TODO category: current goals

	NOTE: these are our most immediate goals for the game. To claim a task, put your name
		next to it (replacing the "UNCLAIMED" and list a few bullet points breaking down what you 
		will need to do to complete the task. You can also add new tasks to this section if you think
		they should be here.

set up materials so that there cannot be cloth weapons, rocks made of anything other
 	than wood, etc (UNCLAIMED)

set up food/hunger penalties (UNCLAIMED)

add burdening penalties (UNCLAIMED)

reorganize the monster class so that monster-only methods are in a separate section 
	(or a separate class altogether) (UNCLAIMED)

massive monster overhaul (Robert)
	*gradient spawning
	*AI types (just add an "unintelligent" one for testing)
	*inventory creation (random monster items, gold, "layouts", etc.)
	*slightly better chasing AI
	*item seeking? (might branch too much into more complex AI)
	*other stuff as I think of it

consider removing the "genericName" private string from the item class (Robert)
	*replace with individual cases returned by genericName()
	
consider making action display scrollable (UNCLAIMED)
	*(should probably happen as part of a larger message system overhaul, 
		which Nick might be working on.)

TODO: sorted tasks

EQUIPMENT STUFF

make all items/monsters use gradient spawning like weapons/armor do (does ammo already do this?)
consider giving the player a prompt on whether or not to unequip items that are in the way of attempted equip items,
	rather than doing so automatically and consuming extra turns.

COVERAGE:

divide monsters into humanoid/non-humanoid. Only humanoid monsters can wear armor and benefit from coverage.

AMMO/RANGED

Determine chance to dodge all projectiles, including spells.
	*might already be partially done

Flesh out monster ranged formulas for damage/toHit/distance (the current values are placeholders)

POTIONS

set up all potions that will be in the game
make some potions more likely to spawn than others. (may involve using dungeon depth)

add message sending to beginStatus() and endStatus() (or whatever they're called) where appropriate.

FOOD/HUNGER

finish implementing hunger. (could take a variety of forms)

TAGS

NOTE: fully setting up tags will be a long and complicated process.

figure out how to set up "tags" like "fireProof", "enchanted", "blessed", etc. (maybe these should be strings from final arrays, with
	booleans that can easily check for specific tags? (like "isTagged(String tag)", which would check for a tag from a final string array.)

ARTIFACTS

artifacts are set up, but require more functionality (like activated abilities, typed damage, etc).
consider giving special powers/tags to artifacts, and decide how to organize these.

MATERIALS

Make special materials to represent spell damage and whatnot, if necessary.

set up final material arrays for branches somehow. (similar to item spawn multipliers)
	*currently, branches have "availableMaterials" which, by default, includes all materials. Change this array somewhere.

WEIGHT

finish implementing all item weights (only food)

NAMING/IDENTIFICATION

fix potion name display (pluralizing goes on the word "potion")
	*Nick is currently tasked with fixing this by setting up more sophisticated grammar/message classes.

Go over implemented name system with Nick, and keep testing identification.
		
Will have to change inventory display messages, pickup messages for items, and other places where item names are called for messages.
Consider handling item name displaying in its own class if it grows complicated enough. (maybe part of "message" or "text" class?)

could make an "inspect" button to let the player roll observations about an item, 
	and base the usefulness/accuracy of these on intelligence or something..

consider a way for player to view all known items, possibly along with other "knowledge".
	*useful for testing and possibly ingame

GENERAL ITEM/SPAWNING STUFF

change all item spawning, similarly to weapon spawning
	*remaining: potions and food
	*break up the itemReader class as necessary.

change monster spawning to match new "monsterDepth" methods and gradient spawning (will have to change MonsterReader; look at
	item readers)
	
implement gradient spawning for monsters and all item types.
	*will have to change min/max depth to a single value. (may want to change all item spawning before doing this step.)
	*spawn chance of a specific monster/item gets lower, approaching 0, with distance from current branch's modified depth for the monster/item.)

IDEA: give monsters equipment via the createWeapon and createArmor methods in the armorReader and WeaponReader classes.
	*make tags in MonsterManual correspond to the proper args, and add a method to call these create methods in MonsterReader.

consider giving monsters items from the item manual(s), rather than programming monster-specific items.
	*keep in mind there will be humanoid and non-humanoid monsters.
	*instead of spawned monsters always getting their "inventory", give each item a different probability for the monster to spawn with it.
	*also consider "A","B","C" etc. equipment "layouts". (example: layout A is a sword and shield, layout B is a two-handed polearm and a helmet.)
	*in the layout case, there could also be "general" items which the monster can always spawn with.
	*figure out how to code monsters holding gold. (Their inventories should be able to hold gold like the player's, but can they actually drop it?)
		*example: test the "dropAllItems() method or whatever it is in the Monster class. Make sure it drops a stack of gold.

fully implement ammo, potions, and food

Consider adding scrolls to the game, or some other item type. (try to be original)

MONSTER STUFF

work on monster AI:
	*monsters should chase player for longer
	*test with monsters that have multiple enemies
	*create more rules for how monsters store their enemy lists, if necessary
	*Consider AI states that allow for neutral interactions with other monsters (such as friendly following)
	*Eventually, create multiple AI types for monsters behaving differently

LEVEL EDITOR

Give the level editor the ability to load files in addition to saving them. (make current screen the loaded file)

figure out how stairs will work in unique levels. 
		*IDEA: save all stair arrays as temp arrays. Then, after overwriting, re-implement these stairs, either writing over stairs added through the
			level editor, adding new stairs (if there are fewer of each stair type than necessary) or removing stairs (if there are too many.)

Figure out how unique levels will work. (they should be read in through the map editor, but the map editor needs to become more complex first.
	*Make sure unique monsters and items can be created and placed via map editor, and that "monster zones" can be laid out, along with
		"monster rosters" of monsters that can spawn in these zones.	
	*Don't automatically place traps in unique levels. (may have to add a "unique" variable first, however. Put it in levelType or something.)
	*can still manually add traps to unique levels.
for unique levels, consider a "unique" boolean which prevents new monsters from spawning, and setting up the level editor with the ability
to add unique monsters. (also consider pulling these monsters/items from a central source, in addition to allowing some randomization.)

SKILLS

continue implementing skills
	*Make other skills useful:
		*Armor/dodge
			*armor skill is currently used in the takeDamage() method for Player, but has no way of being improved and provides no other benefits.
				ask nick if/how this should be changed.
			*Since the armor skill might make armor more effective and/or allow better variety of armor types to be worn without penalties,
				consider making armor more complex before implementing the armor skill.
			*meanwhile, dodge skill is called on the part of the target (if it is the player) during a monster's attack() method.
			*Will eventually need to add in dodging projectiles. (as well as projectile accuracy)	
		*Other spells
			*Have no idea how these will work, or if "evocations" is even the right category for magic missile, the only existing spell.	
		*Elements
			*no idea how elements will work. They have something to do with magic, though.
		*Appraisal
			*Have to give items more data and make an "appraise" command for this to be useful.
	*Adjust bonuses from skills and test their usefulness by plugging various numbers into the formula in fightTester

STATS

figure out how stats will increase, besides drinking potions of gain ability.

ask nick about leveling up increasing current HP, not just max (along with other leveling up stuff)

implement stats on a basic level. stat implementations remaining:
	*Strength
		*carry more (have to implement burdening first)
		*better with heavy weapons (ask Nick: already done? i.e., does "heavy weapons" mean STR category weapons?)
	*dex
		*light and ranged weapons wielded better	(ask Nick: already done? (since there is a DEX category))
	*for
		*shake off poison/sickness/injury faster or be less affected by it (idea: reduce some effects, or small chance to shrug off some effects.)
		*get less tired for less long (what did Nick mean when he said this?????)
	*per
		*Nick said to remove this
	*will
	 	*Better at shaking off confusion/fear/mind-affecting spells/illusions
	 	*a lot better at Dominations, better at Abjurations, Transfigurations.
	*int
		*learn skills faster, learn more spells, greater chance of learning spells
		*a lot better at Evocations, better at Abjurations and Hindrances.
	*luck
		*find better things, spawn easier monsters, less chance of horrible brutality without warning, greater critical hit chance, more luck resource.
		*where a 10 represents average luck, a 9 represents BELOW average luck and means that more shitty things will happen to you.

BRANCHES

consider making the screen wider so "branch display" can always show up on one line.
	*might already be the case. test this.

Test itemManual for branches other than 1. (pretty sure it's implemented though.)
	*reach the final version of itemManuals and readers before doing this.

Find a way to randomize branch lengths and placements, while also ensuring that everything lines up properly. 
	*(No branches should lead above -1. Also, use narrow ranges.)

consider a value for "branch" in monster/item readers which means "all branches" (maybe 0? or -1?)
	*pretty sure this will be the absence of a branch listing

for glitch type branch, make the player display methods return a constantly changing (once per 1/50th of a second) string of letters
at "branch"

GUI STUFF

consider making the action display area scrollable.

Adjust this fightTester class so it opens a massively simplified GUI which can be used to test just a few methods at at time.
	*Idea: a dungeon has a GUI as a stored object and a GUI interfaces with its screens.
	*(First, break up RogueLikeGui into multiple classes. This will be a pain so set aside several hours.)

IDEA: consider giving every GUI screen relative weights for components (like in gridbaglayout, which is used for the main screen) and remove
	screen sizes. this will make it easier for the player to resize the screen.
	
MISC

when player tries to move while paralyzed, remove messages before "you are paralyzed"

"also crafting skill affects this, and same for magic" (what did Nick mean when he said this about materials)	

IDEA: in addition to "roomForRoom" boolean, make "roomInRoom" which returns an int that can be compared to "roomSize" to know whether it
	is reasonable to place more items in a room.
	*alternately, make other methods that limit items per room.

implement bear traps. Consider have them cause an immobility effect.

implement more spells (focus on making different casting types, and plan spell organization ahead of time.)
	*determine how magic skills will affect these spells. (Success rate? effectiveness?)

tweak bare hands damage until it seems right. (will need more weapons from Nick to compare it to, though.)

maybe provide some indication that a weapon is two-handed once it is equipped. 
	(Maybe an X or some other symbol next to off-hand?)

Give spells colors and possibly other visual effects. (consult Nick)

With Nick, conduct an overall waterfall-style plan for the rest of the game's functionality. (make diagrams and ideally figure
	out every class that will be necessary for the rest of the game's development.)

Make special teleporters that send you to a specific other teleporter on the same (or another) level. (add a unique level like this.)
BEN'S IDEA: cloud room
IDEA: add music to the game. (compose it myself)

consider adding messages describing "sounds" (like in nethack) from monsters doing things out of the player's view.

TODO category: things to ask nick about

Go over weapons code with Nick and see if "piercing" should still do anything
should luck be invisible? how to implement it?
cursed/blessed items?
Map editor
	*How to handle stairs?
		*could agree to only put unique levels only in places without stairs branching off or at the end of branches
		*more useful: could have stairs in map editor with "toBranch" data included ("fromBranch" is a property of the entire level.)
			*will have to change level saving/loading, but that will be necessary anyway for unique monsters to become placeable.
			*when reading in a unique level, follow this process:
				*overwrite existing autogenerated level, saving its stair array.
				*read in upStairs, matching toBranch with corresponding toBranch in existing upStair array.
					*if the read-in level runs out of stairs first, (i.e., a searched toBranch cannot be found in the read-in upstairs)
						* keep adding upstairs randomly.	(we want to avoid this, because stairs could end up in awkward places.)
					*if	the existing level runs out of stairs first, (i.e., the read-in upstairs have toBranches that we don't want),
						*replace the unwanted stairs with empty tiles.
				*repeat process for downstairs.
	*How to handle monsters? Unique monsters?
Should potions be limited in scope (i.e., not read in depth and only randomize values on effect generation), or keep working with current formulas?)
	*example: the first case would mean "potions of healing" and separate "potions of extra healing", since each one would have only a limited range.
	*the advantages of the first choice: a) a known potion's effect would be more predictable to the player. b) more powerful/rarer potions would stand out more.
Trap types
identification/inspection system
Luck details? (may need to change item generation a little/add more items to dungeon)
Food? Hunger?
Races? Classes?
other items besides potions: 
	*Scrolls?
	*Spellbooks?
	*Tools? (may require extensive planning compared to other types, since more general)
Locked doors? chests? bags?
stat gains on level-up?
Spells? Elements?
Start Screen?
New level generation types?
Gold?
	*how many stacks? (currently based on empty tiles in level)
	*how much on level 1? level 50? progression?
	*where is gold used? how much is max gold?
	*can gold be dropped?
Monsters:
	*AI patterns?
	*unique monsters?
	*some randomization of loot? (A and B "equipment layouts" that alternately occur on the same monster, rare loot, general loot that drops
		*from a preset pool of "monster loot", (based on monster level and category) etc.
	*monster categories
	*hostilities (monsters are not hostile if the player is the same race as them? etc.)
	*pets?
	*monster special abilities? (should compartmentalize as much as possible, or else put strong/difficult-to-code abilities on unique monsters)
Work out instruction manual with Nick (he may want to write the final version and add some more flavor)

TODO category: unsorted tasks

IDEA: maybe make a "dice" class with 'xdy' methods and range methods for easier randomization.
add object/tile hallucination
			 
IDEA: from main screen, player can press 'esc' key to exit or restart game
TODO: figure out how the GUI handles differently-sized rooms. (Resize window or have the screen center on the player if the entire room isn't visible.)
make sure all rooms are *always* properly connected when a level is created.
decide if it should be possible to drop gold (may be useful if gold is given a weight)

//TODO category: basic functionality goals:

TODO: make a start screen. (Consult Nick. Also, make it a separate class from RogueLikeGui.)
TODO: test monsters that pick up items, possibly on sight
TODO: test ranged monsters. (make sure there are no bugs caused by their "throw" commands.)
TODO: consider a "pick up how many" option. (look at nethack for button)
TODO: when the currentMessage gets too long, truncate it with "..." after the last displayable word. (consider putting this in the "appendToCurrentMessage()" method,
		or make that method call another one which manages the truncation.
		OR make the field scrollable.
TODO: decide what '5' should do in different directional situations. (moving, throwing potion, throwing arrow, etc.)
TODO: add gold to inventory screen (in addition to playerInfo screen)
TODO: make a nicer-looking GUI. (consider using netbeans, or just researching more layout commands.)

	TODO category: Expanding Scope
TODO: finish implementing hunger. add "hungry" to display somewhere as necessary. Also decide how eating works when fullness is not
	completely full, but almost. (consider not having a solid cap, but an area where death from choking becomes increasingly likely, as in
	  Nethack. Also, consider consequences for low hunger. Figure out how to divide hunger states.)
	 
TODO: make it so the most recent item added to a stack is visible on top. (note: is this already the case?)
TODO: expand and organize monster AI. (experiment with neutral monsters and friendly monsters that follow the player.)
TODO: consider implementing a save feature. (consult Nick)
TODO: fully implement all types of spells. (consult Nick)
TODO: fully implement the level-up system. (stat boosts? varying HP boosts? different boosts for different classes/races?) Agree on details with Nick.

TODO: implement NPC interaction. (will have to change player move->attack transition to check if the monster is hostile.)
	//*talking
	//*buying stuff
	//*deciding to attack (consider making a MonsterList class to replace current hostile monsters array
//TODO: if useful, implement monster categories. (human, orc, etc.)
consider different attack messages for different weapons/monsters/etc. (randomization?)

//TODO category game design: (as opposed to coding)
(put ideas here)
*/
import java.awt.Dimension;
import java.io.FileNotFoundException;
import java.util.Random;
import java.util.Scanner;

import javax.swing.JFrame;
import javax.xml.stream.XMLStreamException;

public class FightTester {
  
	public static void main(String[] args){
		
		for(int i=0; i<10;i++){
			System.out.println(Material.spawnableMaterials[i]);
		}
		//System.out.println(Material.getMaterial("iron").breakRate());
		//System.out.println(Material.getMaterial("iron").getMultipliers()[1]);
	}
		//TODO: perform tests here.
	
	static Random dice=new Random();
}