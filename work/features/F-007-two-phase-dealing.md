# F-007: Two-phase dealing with chooser decision

* Type: Feature
* Status: In Progress

## Description
Introduce mariáš-style two-phase dealing where the chooser receives
a partial hand, selects trump (or contract), and then receives remaining cards.

## Success Criteria
- Engine supports paused dealing
- Chooser decision blocks progression
- Server exposes decision endpoints
- UI supports trump selection

## Related Tasks
- T-007 Engine + server basic support
- T-008 Generic chooser decision gate
- T-009 Deal pattern validation
- T-010 REST API cleanup
- T-011 Deal order visibility
- T-012 Web UI
