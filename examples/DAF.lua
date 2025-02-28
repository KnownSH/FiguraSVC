--[[
DAF (Derg Auditory Framework)
Shrimple framework for making FiguraSVC easier to program with.
Works on 1.5

(This is a highly commented example framework for working with FiguraSVC 2.0)
]] 

--- @class DAF
--- @field protected microphoneOffTIme integer
--- @field protected clock integer
local DAF = {}
DAF.microphoneOffTime = 0
DAF.clock = 0
DAF.isMicrophoneOn = false

--- @alias PCM number[]
--- Raw PCM data from SVC

--- Set a function that modifies the audio signal (W.I.P-ish)
--- @param modifer fun(audio: PCM)
function DAF.setAudioModifer(modifer)
  DAF.modifier = modifer
end

--- Set a function to trigger avatar animations
--- @param animator fun(audio: PCM)
function DAF.setAnimation(animator)
  DAF.animator = animator
end

--- Set a fallback animation if another client other than the host does not have FiguraSVC installed
--- This works almost exactly like the FiguraSVC 1.1 system
--- @param ping fun(state: boolean)
function DAF.setFallbackPing(ping)
  DAF.ping = ping
end

--- Set a function thats called when the microphone is considered to be off (no recent voice activity)
--- @param off function
function DAF.setOnMicrophoneOff(off)
  DAF.off = off
end

--- @return boolean Returns true if initialization is successful, otherwise false
local function runtime()
  if not client:isModLoaded("voicechat") then return end -- Exit if Simple Voice Chat mod is not loaded

  function events.tick()
    local previousMicState = DAF.isMicrophoneOn

    -- Determine if the microphone is considered "on". Microphone is "on" if voice activity was detected within the last 2 gameticks
    DAF.microphoneOffTime = DAF.microphoneOffTime + 1
    DAF.isMicrophoneOn = DAF.microphoneOffTime <= 2

    -- check if the microphone state has changed
    if previousMicState ~= DAF.isMicrophoneOn then
      if not client:isModLoaded("figurasvc") then -- Check if FiguraSVC is [NOT] loaded
        DAF.ping(DAF.isMicrophoneOn) -- Use old-school fallback ping
      end
      if not DAF.isMicrophoneOn then
        DAF.off()
      end
    end
  end
  
  -- check if FiguraSVC is loaded
  if client:isModLoaded("figurasvc") then
    -- hooks
    --- @function events.host_microphone
    --- @param audio PCM
    function events.host_microphone(audio) -- only runs on host
      pcall(function() -- Error Prevention (happened mostly on dev builds of Figura 1.5)
        DAF.microphoneOffTime = 0
        if DAF.animator ~= nil then DAF.animator(audio) end
        if DAF.modifier then DAF.modifier(audio) end
      end)
    end

    local debounce = false

    --- @function events.microphone
    --- @param playername string
    --- @param audio PCM
    function events.microphone(playername, audio) -- runs on all clients besides host
      if debounce then
        if DAF.animator then DAF.animator(audio) end
      end
      debounce = not debounce
    end
  end
  return true
end

return {
  events = DAF,
  init = runtime
}
